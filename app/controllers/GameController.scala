package controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import clients.actors.{WebSocketClientActor, WsInbound, WsOutbound}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

@Singleton
class GameController @Inject()(@Named("pimp-actor") pimpActor: ActorRef, cc: ControllerComponents)
                              (implicit system: ActorSystem, mat: Materializer) extends InjectedController() {

  implicit val inFormat = WsInbound.format
  implicit val outFormat = WsOutbound.format
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[WsInbound, WsOutbound]

  def socket: WebSocket = WebSocket.accept[WsInbound, WsOutbound] { _ =>
    ActorFlow.actorRef { out =>
      WebSocketClientActor.props(pimpActor, out)
    }
  }
}
