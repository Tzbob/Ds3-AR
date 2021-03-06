package ds3ar.ui

import ds3ar.ir._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global

import scala.scalajs.js.typedarray._

object AjaxResourceDataManager {
  def equipParamWeaponManagerFor(version: String) = {
    val base = s"."
    ResourceDataManager.equipParamWeaponManagerFor(streamer, base, version)
  }

  private val streamer = (name: String) => {
    Ajax.get(url = name, responseType = "arraybuffer")
      .map(_.response.asInstanceOf[ArrayBuffer])
      .map(x => new ArrayBufferInputStream(x))
  }
}
