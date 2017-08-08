package com.typesafe.sbt
package packager
package rpm

import sbt._
import sbt.Keys.sourceDirectory
import rpm.Keys._
import linux._
import java.nio.charset.Charset

/** Plugin trait containing all generic values used for packaging linux software. */
trait RpmPlugin extends Plugin with LinuxPlugin {
  val Rpm = config("rpm") extend Linux

  import RpmPlugin.Names

  def rpmSettings: Seq[Setting[_]] = Seq(
    rpmOs := "Linux", // TODO - default to something else?
    rpmRelease := "0",
    rpmPrefix := None,
    rpmVendor := "", // TODO - Maybe pull in organization?
    rpmLicense := None,
    rpmDistribution := None,
    rpmUrl := None,
    rpmGroup := None,
    rpmPackager := None,
    rpmIcon := None,
    rpmAutoprov := "yes",
    rpmAutoreq := "yes",
    rpmProvides := Seq.empty,
    rpmRequirements := Seq.empty,
    rpmPrerequisites := Seq.empty,
    rpmObsoletes := Seq.empty,
    rpmConflicts := Seq.empty,
    rpmPretrans := None,
    rpmPre := None,
    rpmPost := None,
    rpmVerifyscript := None,
    rpmPosttrans := None,
    rpmPreun := None,
    rpmPostun := None,
    rpmChangelogFile := None,
    rpmBrpJavaRepackJars := false,
    rpmScriptsDirectory <<= sourceDirectory apply (_ / "rpm" / Names.Scriptlets),
    // Explicitly defer  default settings to generic Linux Settings.
    packageSummary in Rpm <<= packageSummary in Linux,
    packageDescription in Rpm <<= packageDescription in Linux,
    target in Rpm <<= target(_ / "rpm"),
    name in Rpm <<= name in Linux,
    packageName in Rpm <<= packageName in Linux,
    executableScriptName in Rpm <<= executableScriptName in Linux
  ) ++ inConfig(Rpm)(Seq(
      packageArchitecture := "noarch",
      rpmMetadata <<=
        (packageName, version, rpmRelease, rpmPrefix, packageArchitecture, rpmVendor, rpmOs, packageSummary, packageDescription, rpmAutoprov, rpmAutoreq) apply RpmMetadata,
      rpmDescription <<=
        (rpmLicense, rpmDistribution, rpmUrl, rpmGroup, rpmPackager, rpmIcon, rpmChangelogFile) apply RpmDescription,
      rpmDependencies <<=
        (rpmProvides, rpmRequirements, rpmPrerequisites, rpmObsoletes, rpmConflicts) apply RpmDependencies,
      rpmPre <<= (rpmPre, rpmBrpJavaRepackJars) apply {
        case (pre, true) => pre
        case (pre, false) =>
          val scriptBits = IO.readStream(RpmPlugin.osPostInstallMacro.openStream, Charset forName "UTF-8")
          Some(pre.map(_ + "\n").getOrElse("") + scriptBits)
      },
      rpmScripts <<=
        (rpmPretrans, rpmPre, rpmPost, rpmVerifyscript, rpmPosttrans, rpmPreun, rpmPostun) apply RpmScripts,
      rpmSpecConfig <<=
        (rpmMetadata, rpmDescription, rpmDependencies, rpmScripts, linuxPackageMappings, linuxPackageSymlinks) map RpmSpec,
      packageBin <<= (rpmSpecConfig, target, streams) map { (spec, dir, s) =>
        spec.validate(s.log)
        RpmHelper.buildRpm(spec, dir, s.log)
      },
      rpmLint <<= (packageBin, streams) map { (rpm, s) =>
        (Process(Seq("rpmlint", "-v", rpm.getAbsolutePath)) ! s.log) match {
          case 0 => ()
          case x => sys.error("Failed to run rpmlint, exit status: " + x)
        }
      }
    ))
}

object RpmPlugin {

  def osPostInstallMacro: java.net.URL = getClass getResource "brpJavaRepackJar"

  object Names {
    val Scriptlets = "scriptlets"

    //maintainer script names
    val Post = "postinst"
    val Pre = "preinst"
    val Postun = "postun"
    val Preun = "preun"
  }
}
