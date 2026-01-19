module.exports = {
  apps: [
    {
      name: "springboot-unischedule-service",
      script: "java",
      args: ["-jar", "target/UniScheduleService-0.0.1-SNAPSHOT.jar"],
      cwd: "/Users/vuvanhoang/WorkSpace/project/unischedule/unischedule_service",
      env: {
        SPRING_PROFILES_ACTIVE: "prod"
      }
    }
  ]
};
