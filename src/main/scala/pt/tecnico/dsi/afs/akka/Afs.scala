package pt.tecnico.dsi.afs.akka

import java.io.File

import pt.tecnico.dsi.afs.{ErrorCase, Permission, Quota}
import squants.information.Information

/**
  *
  */
object Afs {
  type DeliveryId = Long

  sealed trait Request {
    def deliveryId: DeliveryId
  }
  //If removeId is a Some then just the result for (senderPath, removeId) is removed
  //Otherwise all the results for the senderPath are removed.
  case class RemoveDeduplicationResult(removeId: Option[DeliveryId], deliveryId: DeliveryId) extends Request

  //region <FS requests>
  case class ListQuota(directory: File, deliveryId: DeliveryId) extends Request
  case class SetQuota(directory: File, quota: Information, deliveryId: DeliveryId) extends Request
  case class ListMount(directory: File, deliveryId: DeliveryId) extends Request
  case class MakeMount(directory: File, volume: String, deliveryId: DeliveryId) extends Request
  case class RemoveMount(directory: File, deliveryId: DeliveryId) extends Request
  case class CheckVolumes(deliveryId: DeliveryId) extends Request
  case class FlushAll(deliveryId: DeliveryId) extends Request
  case class ListACL(directory: File, deliveryId: DeliveryId) extends Request
  case class SetACL(directory: File, acls: Map[String, Permission], negative: Boolean = false, deliveryId: DeliveryId) extends Request
  //endregion

  //region <PTS requests>
  case class GetGroupOrUserId(username: String, deliveryId: DeliveryId) extends Request
  case class GetGroupOrUserName(id: Int, deliveryId: DeliveryId) extends Request
  case class CreateUser(name: String, afsId: Int, deliveryId: DeliveryId) extends Request
  case class CreateGroup(name: String, owner: String, deliveryId: DeliveryId) extends Request
  case class DeleteUserOrGroup(name: String, deliveryId: DeliveryId) extends Request
  case class AddUserToGroup(name: String, group: String, deliveryId: DeliveryId) extends Request
  case class RemoveUserFromGroup(name: String, group: String, deliveryId: DeliveryId) extends Request
  case class Membership(name: String, deliveryId: DeliveryId) extends Request
  case class ListGroups(deliveryId: DeliveryId) extends Request
  //endregion

  //region <VOS requests>
  case class CreateVolume(server:String, partition: String, name:String, maxQuota: Information, deliveryId: DeliveryId) extends Request
  case class RemoveVolume(server:String, partition: String, name:String, deliveryId: DeliveryId) extends  Request
  case class AddSite(server:String, partition:String, name:String, deliveryId: DeliveryId) extends Request
  case class ReleaseVolume(name:String, deliveryId: DeliveryId) extends Request
  case class ExamineVolume(name:String, deliveryId: DeliveryId) extends Request
  case class VolumeExists(name:String, server:String, deliveryId: DeliveryId) extends Request
  case class BackupVolume(name:String, deliveryId: DeliveryId) extends Request
  //endregion

  sealed trait Response {
    def deliveryId: DeliveryId
  }

  sealed trait SuccessResponse extends Response

  //region <FS responses>
  case class Successful(deliveryId: DeliveryId) extends SuccessResponse

  case class ListQuotaResponse(quota: Quota, deliveryId: DeliveryId) extends SuccessResponse
  case class SetQuotaResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class ListMountResponse(vol:String, deliveryId: DeliveryId) extends SuccessResponse
  case class MakeMountResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class RemoveMountResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class CheckVolumesResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class FlushAllResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class ListACLResponse(list:(Map[String, Permission], Map[String, Permission]), deliveryId: DeliveryId) extends SuccessResponse
  case class SetACLResponse(deliveryId: DeliveryId) extends SuccessResponse
  //endregion

  //region <PTS responses>
  case class GetGroupOrUserIdResponse(id: Int, deliveryId: DeliveryId)  extends SuccessResponse
  case class GetGroupOrUserNameResponse(name:String, deliveryId: DeliveryId) extends SuccessResponse
  case class CreateUserResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class CreateGroupResponse(id: Int, deliveryId: DeliveryId) extends SuccessResponse
  case class DeleteUserOrGroupResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class AddUserToGroupResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class RemoveUserFromGroupResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class MembershipResponse(members: Set[String], deliveryId: DeliveryId) extends SuccessResponse
  case class ListGroupsResponse(groups: Seq[(String, Int, Int, Int)], deliveryId: DeliveryId) extends SuccessResponse
  //endregion

  //region <VOS responses>
  case class CreateVolumeResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class RemoveVolumeResponse(deliveryId: DeliveryId) extends  SuccessResponse
  case class AddSiteResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class ReleaseVolumeResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class ExamineVolumeResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class VolumeExistsResponse(deliveryId: DeliveryId) extends SuccessResponse
  case class BackupVolumeResponse(deliveryId: DeliveryId) extends SuccessResponse
  //endregion


  sealed trait FailureResponse extends Response
  case class Failed(errorCase: ErrorCase, deliveryId: DeliveryId) extends FailureResponse
}
