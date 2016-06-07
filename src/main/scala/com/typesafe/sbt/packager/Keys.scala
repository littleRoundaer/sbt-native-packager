package com.typesafe.sbt
package packager

import sbt._

object Keys extends linux.Keys 
  with debian.DebianKeys 
  with rpm.RpmKeys 
  with windows.WindowsKeys
  with universal.UniversalKeys {
  
  // TODO - Do these keys belong here?
  val makeBashScript = TaskKey[Option[File]]("makeBashScript", "Creates or discovers the bash script used by this project.")
  val makeBatScript = TaskKey[Option[File]]("makeBatScript", "Creates or discovers the bat script used by this project.")
  
}