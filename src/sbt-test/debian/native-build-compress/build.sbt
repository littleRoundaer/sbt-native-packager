enablePlugins(DebianPlugin)

debianNativeBuildOptions in Debian := Nil

maintainer := "Maintainer <maintainer@example.com>"

packageDescription := "Description"

packageSummary := "Summary"

TaskKey[Unit]("check-deb-compression") := {
  val deb = target.value / s"${(name in Debian).value}_${(version in Debian).value}_all.deb"
  assert(Seq("ar", "-t", deb.toString).lines.exists(_.startsWith("data.tar."))) // exact extension varies by dpkg-deb version
}
