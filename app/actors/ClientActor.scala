package actors

import javax.inject._

import akka.actor._
import akka.event.LoggingReceive
import com.google.inject.assistedinject.Assisted
import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._

class ClientActor @Inject()(@Assisted out: ActorRef,
                            @Named("messageHubActor") messageHubActor: ActorRef, configuration: Configuration) extends Actor with ActorLogging {
  import ClientActor._
  
  override def preStart(): Unit = {
    super.preStart()
  }

  override def receive: Receive = LoggingReceive {
    case Entry =>
      log.debug("Entry:" + sender)
      messageHubActor ! MessageHubActor.Entry
    case State(s) =>
      log.debug("State:" + sender + " / " + s)
      val stateUpdateMessage = Json.obj("type" -> "stateupdate", "state" -> s)
      out ! stateUpdateMessage
    case json: JsValue =>
      log.debug("json:" + sender + " / " + json)
      // When the user types in a stock in the upper right corner, this is triggered
      val state = (json \ "state")
      messageHubActor ! MessageHubActor.State(state.asOpt[String].getOrElse(state.as[Int].toString))
  }
}

class ClientManagerActor @Inject()(childFactory: ClientActor.Factory) extends Actor with InjectedActorSupport with ActorLogging {
  import ClientManagerActor._

  override def receive: Receive = LoggingReceive {
    case Create(id, out) =>
      val child: ActorRef = injectedChild(childFactory(out), s"clientActor-$id")
      child ! ClientActor.Entry
      sender() ! child
  }
}

object ClientManagerActor {
  case class Create(id: String, out: ActorRef)
}

object ClientActor {
  trait Factory {
    // Corresponds to the @Assisted parameters defined in the constructor
    def apply(out: ActorRef): Actor
  }
  
  case object Entry
  case class State(s: String)
  
}
