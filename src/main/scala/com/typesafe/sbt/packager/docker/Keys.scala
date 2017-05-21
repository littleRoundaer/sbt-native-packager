package com.typesafe.sbt
package packager
package docker

import sbt._

trait DockerKeys {
  val dockerGenerateConfig = TaskKey[File]("docker-generate-config", "Generates configuration file for Docker.")
  val dockerGenerateContext = TaskKey[File]("docker-generate-context", "Generates context directory for Docker.")
  val dockerPackageMappings = TaskKey[Seq[(File, String)]]("docker-package-mappings", "Generates location mappings for Docker build.")

  val dockerBaseImage = SettingKey[String]("dockerBaseImage", "Base image for Dockerfile.")
  val dockerExposedPorts = SettingKey[Seq[Int]]("dockerExposedPorts", "Ports exposed by Docker image")
  val dockerExposedVolumes = SettingKey[Seq[String]]("dockerExposedVolumes", "Volumes exposed by Docker image")
}

object Keys extends DockerKeys {
  def cacheDirectory = sbt.Keys.cacheDirectory
  def mappings = sbt.Keys.mappings
  def name = sbt.Keys.name
  def publishArtifact = sbt.Keys.publishArtifact
  def sourceDirectory = sbt.Keys.sourceDirectory
  def target = sbt.Keys.target
  def defaultLinuxInstallLocation = packager.Keys.defaultLinuxInstallLocation
  def normalizedName = universal.Keys.normalizedName
  def stage = universal.Keys.stage
  def daemonUser = linux.Keys.daemonUser
  def maintainer = linux.Keys.maintainer
}
