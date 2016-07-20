package pt.tecnico.dsi.afs.akka

import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}
import pt.tecnico.dsi.afs.{Settings => KadminSettings}
import scala.concurrent.duration.Duration

/**
  *
  */
class Settings(config: Config = ConfigFactory.load()) {
  val afsKadminConfig: Config = {
    val reference = ConfigFactory.defaultReference()
    val finalConfig = config.withFallback(reference)
    finalConfig.checkValid(reference, "akka-afs")
    finalConfig.getConfig("akka-afs")
  }
  import afsKadminConfig._

  val removeDelay = Duration(getDuration("remove-delay", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)

  val saveSnapshotRoughlyEveryXMessages = getInt("save-snapshot-roughly-every-X-messages")

  val afsSettings: KadminSettings = {
    val path = "afs"
    if (afsKadminConfig.hasPath(path)) {
      val c = if (config.hasPath(path)) {
        afsKadminConfig.getConfig(path).withFallback(config.getConfig(path))
      } else {
        afsKadminConfig.getConfig(path)
      }
      new KadminSettings(c.atPath(path))
    } else if (config.hasPath(path)) {
      new KadminSettings(config.getConfig(path).atPath(path))
    } else {
      new KadminSettings()
    }
  }
  override def toString: String = afsKadminConfig.root.render
}
