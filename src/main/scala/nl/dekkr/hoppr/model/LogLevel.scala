package nl.dekkr.hoppr.model

/**
 * The different loglevels used in the FetchLog
 */
sealed trait LogLevel
case object Critical extends LogLevel
case object Error extends LogLevel
case object Warning extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel


