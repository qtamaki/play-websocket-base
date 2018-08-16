package actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive
import scala.collection.mutable.HashSet
import akka.actor.ActorRef

class MessageHubActor extends Actor with ActorLogging {
  import MessageHubActor._

  protected[this] var entries: HashSet[ActorRef] = HashSet.empty[ActorRef]

  def receive = LoggingReceive {
    case Entry =>
      entries = entries + sender
    case Exit =>
      entries = entries - sender
    case State(s) =>
      entries.foreach(_ ! ClientActor.State(s))
  }

}

object MessageHubActor {
  case object Entry
  case object Exit
  case class State(s: String)
  
}