package pt.tecnico.dsi.kadmin.afs

import java.io.File

import pt.tecnico.dsi.afs.akka.Afs._
import pt.tecnico.ulisboa.afs.akka.ActorSysSpec
import squants.information.InformationConversions._

class DeduplicationSpec extends ActorSysSpec {
  val policyName = "deduplication"
  val principalName = "withDeduplicationPrincipal"

  "The side-effect" must {
    "only be executed once" when {

      "messages are sent in a ping-pong manner" in {
        val dir = new File("/afs/.example.com")
        val newQuota = 10.mebibytes
        afsActor ! ListQuota(dir, nextSeq())
        expectMsgClass(classOf[ListQuotaResponse])

        afsActor ! SetQuota(dir, newQuota, nextSeq())
        expectMsgClass(classOf[SetQuotaResponse])

        afsActor ! ListQuota(dir, nextSeq())
        expectMsgClass(classOf[ListQuotaResponse])
      }


      "messages are sent in rapid succession" in {
        val dir = new File("/afs/.example.com")
        val newQuota = 10.mebibytes
        afsActor ! ListQuota(dir, nextSeq())
        afsActor ! SetQuota(dir, newQuota, nextSeq())
        afsActor ! ListQuota(dir, nextSeq())

        expectMsgClass(classOf[ListQuotaResponse])
        expectMsgClass(classOf[SetQuotaResponse])
        expectMsgClass(classOf[ListQuotaResponse])

      }
    }
  }
}
