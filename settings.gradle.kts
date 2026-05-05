pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 👇 이 줄을 꼭 추가해 주세요! (kts 방식은 괄호와 큰따옴표를 씁니다)
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "MotJip"
include(":app")
 