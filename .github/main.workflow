workflow "Build, Push Image" {
  on = "push"
  resolves = ["Install: UI"]
}

action "Install: UI" {
  uses = "gmatheu/action-maven-cli@oracle-jdk-8"
  args = "-f ui/pom.xml install"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
}
