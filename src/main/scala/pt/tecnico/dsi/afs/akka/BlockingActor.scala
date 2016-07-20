package pt.tecnico.dsi.afs.akka

import java.io.File

import akka.actor.{Actor, ActorLogging}
import pt.tecnico.dsi.afs.akka.Afs._
import pt.tecnico.dsi.afs.{Settings => AFSSettings}
import pt.tecnico.dsi.afs.{ErrorCase, Permission, Quota, UnknownError, AFS => AFSCore}
import work.martins.simon.expect.core.Expect

import scala.concurrent.Await
import scala.util.{Failure, Success, Try}
import pt.tecnico.dsi.afs.akka.AfsActor.{Retry, SideEffectResult}
/**
  *
  */
class BlockingActor(val afsSettings: AFSSettings) extends Actor with ActorLogging  {
  val scalaExpectTimeout = afsSettings.scalaExpectSettings.timeout

  val afs = new AFSCore()

  //We will run the expects in this ExecutionContext
  import context.dispatcher


  def runExpect[R](deliveryId: DeliveryId, expect: ⇒ Expect[Either[ErrorCase, R]], mapResult: R => Response): Unit = {
    Try {
      //The expect creation might fail if the arguments to the operation are invalid.
      expect
    } match {
      case Success(e) =>
        val f = e.run() map {
          case Right(r) => mapResult(r)
          //ListQuotaResponse(quota, deliveryId)
          //case Right(()) => Successful(deliveryId)
          //case Right(name: String) => StringResponse

          case Left(ec: ErrorCase) => Failed(ec, deliveryId)
        } recover {
          //Most probably the expect failed due to a TimeoutException and there isn't a when(timeout) declared
          case t: Throwable => Failed(UnknownError(Some(t)), deliveryId)
        }

        //We wait 3*scalaExpectTimeout because the expect might be composed with other expects (with returningExpect or flatMap)
        context.parent ! SideEffectResult(sender(), Await.result(f, 3 * scalaExpectTimeout))
      case Failure(t) =>
        context.parent ! SideEffectResult(sender(), Failed(UnknownError(Some(t)), deliveryId))
    }
  }

  def receive: Receive = {
    case r: Retry => context.parent forward r
    //region <FS requests>
    case ListQuota(directory, deliveryId) =>
      runExpect(deliveryId, afs.listQuota(directory), (q:Quota) => ListQuotaResponse(q, deliveryId))
    case SetQuota(directory, quota, deliveryId) =>
      runExpect(deliveryId, afs.setQuota(directory,quota), (ret:Unit) => SetQuotaResponse(deliveryId))
    case ListMount(file, deliveryId) =>
      runExpect(deliveryId, afs.listMount(file), (mnt:String) => ListMountResponse(mnt, deliveryId))
    case MakeMount(dir,vol,deliveryId) =>
      runExpect(deliveryId, afs.makeMount(dir,vol), (ret:Unit) => MakeMountResponse(deliveryId))
    case RemoveMount(dir,deliveryId) =>
      runExpect(deliveryId, afs.removeMount(dir), (ret:Unit) => RemoveMountResponse(deliveryId))
    case CheckVolumes(deliveryId) =>
      runExpect(deliveryId, afs.checkVolumes(), (ret:Unit) => CheckVolumesResponse(deliveryId))
    case FlushAll(deliveryId) =>
      runExpect(deliveryId, afs.flushAll(), (ret:Unit) => FlushAllResponse(deliveryId))
    case ListACL(dir, deliveryId) =>
      runExpect(deliveryId, afs.listACL(dir),
        (acl: (Map[String, Permission], Map[String, Permission])) => ListACLResponse(acl, deliveryId))
    case SetACL(dir, acls, negative, deliveryId) =>
      runExpect(deliveryId, afs.setACL(dir, acls, negative), (ret:Unit) => SetACLResponse(deliveryId))
    //endregion
    //region <PTS requests>
    case GetGroupOrUserId(username, deliveryId) =>
      runExpect(deliveryId, afs.getGroupOrUserId(username), (id:Int) => GetGroupOrUserIdResponse(id, deliveryId))
    case GetGroupOrUserName(id, deliveryId) =>
      runExpect(deliveryId, afs.getGroupOrUserName(id), (username:String) => GetGroupOrUserNameResponse(username, deliveryId))
    case CreateUser(name, afsId, deliveryId) =>
      runExpect(deliveryId, afs.createUser(name,afsId), (ret:Unit) => CreateUserResponse(deliveryId))
    case CreateGroup(name, owner, deliveryId) =>
      runExpect(deliveryId, afs.createGroup(name, owner), (id:Int) => CreateGroupResponse(id, deliveryId))
    case DeleteUserOrGroup(name, deliveryId) =>
      runExpect(deliveryId, afs.deleteUserOrGroup(name), (ret:Unit) => DeleteUserOrGroupResponse(deliveryId))
    case AddUserToGroup(name, group, deliveryId) =>
      runExpect(deliveryId, afs.addUserToGroup(name, group), (ret:Unit) => AddUserToGroupResponse(deliveryId))
    case RemoveUserFromGroup(name, group, deliveryId) =>
      runExpect(deliveryId, afs.removeUserFromGroup(name, group), (ret:Unit) => RemoveUserFromGroupResponse(deliveryId))
    case Membership(name, deliveryId) =>
      runExpect(deliveryId, afs.membership(name), (m:Set[String]) => MembershipResponse(m,deliveryId))
    case ListGroups(deliveryId) =>
      runExpect(deliveryId, afs.listGroups(), (groups: Seq[(String,Int,Int,Int)]) => ListGroupsResponse(groups, deliveryId))
    //endregion
    //region <VOS requests>
    case CreateVolume(server, partition, name, maxQuota, deliveryId) =>
      runExpect(deliveryId, afs.createVolume(server,partition,name,maxQuota),
        (ret:Unit) => CreateVolumeResponse(deliveryId))
    case RemoveVolume(server, partition, name, deliveryId) =>
      runExpect(deliveryId, afs.removeVolume(server, partition, name), (ret:Unit) => RemoveVolumeResponse(deliveryId))
    case AddSite(server, partition, name, deliveryId) =>
      runExpect(deliveryId, afs.addSite(server,partition,name), (ret:Unit) => AddSiteResponse(deliveryId))
    case ReleaseVolume(name, deliveryId) =>
      runExpect(deliveryId, afs.releaseVolume(name), (ret:Unit) => RemoveVolumeResponse(deliveryId))
    case ExamineVolume(name, deliveryId) =>
      runExpect(deliveryId, afs.examineVolume(name), (ret:Unit) => ExamineVolumeResponse(deliveryId))
    case VolumeExists(name, server, deliveryId) =>
      runExpect(deliveryId, afs.volumeExists(name, server), (ret:Unit) => VolumeExistsResponse(deliveryId))
    case BackupVolume(name, deliveryId) =>
      runExpect(deliveryId, afs.backupVolume(name), (ret:Unit) => BackupVolumeResponse(deliveryId))
    //endregion

  }
}
