package pt.tecnico.ulisboa.pt

import java.io.File

import pt.tecnico.dsi.afs.Quota
import pt.tecnico.dsi.afs.akka.Afs._
import squants.information.InformationConversions._

/**
  *
  */

class AfsSpec extends ActorSysSpec {
  "listQuota" should "succeed".in {
    val dir = new File("/afs/.example.com")
    val id1 = nextSeq()
    afsActor ! ListQuota(dir, id1)
    val r1 = expectMsgClass(classOf[ListQuotaResponse])
    r1.deliveryId shouldBe id1

    val id2 = nextSeq()
    val newQuota = 10.mebibytes
    afsActor ! SetQuota(dir, newQuota, id2)
    expectMsgClass(classOf[SetQuotaResponse])

    val id3 = nextSeq()
    afsActor ! ListQuota(dir, id3)
    val r2 = expectMsgClass(classOf[ListQuotaResponse])
    r2.deliveryId shouldBe id3
    r2.quota.quota shouldBe newQuota


  }

  "an invalid operation" should "return failed".in {
    val id = nextSeq()
    val invalidDir = new File("/afs/zzz")
    afsActor ! ListQuota(invalidDir, id)
    val failed = expectMsgClass(classOf[Failed])
    failed.deliveryId shouldBe id
  }
}
