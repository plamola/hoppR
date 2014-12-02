package nl.nl.dekkr.hoppr.model



/**
 * Created by Matthijs Dekker on 25/11/14.
 */
sealed trait LogLevel
case object Critical extends LogLevel
case object Error extends LogLevel
case object Warning extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel


