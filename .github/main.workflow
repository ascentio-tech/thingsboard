workflow "Build, Push Image" {
  on = "push"
  resolves = ["Install: UI"]
}

action "Install: UI" {
  uses = "gmatheu/action-maven-cli@d2fe9c5"
  args = "-f ui/pom.xml install"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
}
