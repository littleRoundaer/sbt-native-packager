enablePlugins(RpmPlugin)

name := "rpm-test"

version := "0.1.0"

maintainer := "Josh Suereth <joshua.suereth@typesafe.com>"

packageSummary := "Test rpm package"

packageDescription := """A fun package description of our software,
  with multiple lines."""

rpmRelease := "1"

rpmVendor := "typesafe"

rpmUrl := Some("http://github.com/sbt/sbt-native-packager")

rpmLicense := Some("BSD")

packageArchitecture in Rpm := "x86_64"

linuxPackageMappings := configWithNoReplace(linuxPackageMappings.value)

TaskKey[Unit]("unzip") <<= (packageBin in Rpm, streams) map { (rpmFile, streams) =>
  val rpmPath = Seq(rpmFile.getAbsolutePath)
  Process("rpm2cpio", rpmPath) #| Process("cpio -i --make-directories") ! streams.log
}

TaskKey[Unit]("checkSpecFile") <<= (target, streams) map { (target, out) =>
  val spec = IO.read(target / "rpm" / "SPECS" / "rpm-test.spec")

  assert(
    spec contains
      "%files\n%dir %attr(0755,root,root) /usr/share/rpm-test/conf",
    "Contains configuration directory."
  )

  assert(
    spec contains
      "%config(noreplace) %attr(0644,root,root) /usr/share/rpm-test/conf/test",
    "Sets custom config to 'noreplace'"
  )

  out.log.success("Successfully tested rpm test file")
  ()
}
