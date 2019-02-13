workflow "Build Image" {
  on = "push"
  resolves = ["UI: npm install", "UI: Install", "Package"]
}

action "UI: npm install" {
  uses = "actions/npm@4633da3702a5366129dca9d8cc3191476fc3433c"
  args = "npm install --prefix ui ui"
}

action "UI: Install" {
  uses = "gmatheu/action-maven-cli@d2fe9c5"
  args = "-f ui/pom.xml install"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
  needs = ["UI: npm install"]
}

action "Package" {
  uses = "gmatheu/action-maven-cli@d2fe9c5"
  args = "-DblackBoxTests.skip=true -DskipTests=true package"
  env = {
    ACCEPT_ORACLE_BCLA = "true"
  }
  needs = ["UI: Install"]
}
