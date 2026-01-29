module.exports = {
    apps: [

        {
            name: "springboot-unischedule-service",
            script: "java",
            args: "-jar target/UniScheduleService-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod",
            cwd: "/Users/vuvanhoang/WorkSpace/project/unischedule/unischedule_service"
        }
    ]
};
