package pt.tecnico.ulisboa.afs.akka

import java.io.File

import pt.tecnico.dsi.afs.akka.Afs._
import squants.information.InformationConversions._
/**
  *
  */
class RemoveAndResendSpec extends ActorSysSpec {
  val dir = new File("/afs/.example.com")
  val newQuota = 10.mebibytes
  val requests = Seq(
    ListQuota(dir, nextSeq()),
    SetQuota(dir, newQuota, nextSeq()),
    ListQuota(dir, nextSeq())
  )

  var responses: Seq[Response] = Seq.empty[Response]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    responses = requests.map { request =>
      afsActor ! request
      val msg = expectMsgClass(classOf[Response])
      msg.deliveryId shouldBe request.deliveryId
      msg
    }
  }

  "resend" should "succeed".in {
    afsActor ! requests(1)
    val msg = expectMsgClass(classOf[Response])
    msg shouldEqual responses(1)
  }

  "remove" should "succeed".in {
    val removeId = nextSeq()
    afsActor ! RemoveDeduplicationResult(Some(requests(1).deliveryId), removeId)
    expectMsg(Successful(removeId))

    afsActor ! requests(1)
    expectNoMsg()

    afsActor ! requests(2)
    expectMsgClass(classOf[Response]) shouldEqual responses(2)
  }
}
