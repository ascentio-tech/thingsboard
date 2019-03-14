workflow "Build Image" {
  on = "push"
  resolves = ["UI: Install", "Package", "Docker Login"]
}

action "UI: Install" {
  uses = "gmatheu/action-maven-cli@d2fe9c5"
  args = "-f ui/pom.xml install"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
}

action "Package" {
  uses = "gmatheu/action-maven-cli@d2fe9c5"
  args = "-DblackBoxTests.skip=true -DskipTests=true -Ddockerfile.skip=false -Ddocker.repo=ascentiotech -Ppush-docker-image install"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
  needs = ["UI: Install", "Docker Login"]
}

action "Docker Login" {
  uses = "actions/docker/login@master"
  secrets = ["DOCKER_USERNAME", "DOCKER_PASSWORD"]
}
