# FIXES.md

## Overview

This document records issues identified in the supplied starter repository, the remediation applied, and the rationale for each change.

The aim is to improve security, reliability, reproducibility and deployment quality without introducing unnecessary application-level changes outside the scope of the exercise.

---

## Dockerfile

### Issue: Unpinned Maven base image

**Current state:** The Dockerfile uses `maven:latest`.

**Fix:** Replace the floating `latest` image with a pinned Java/Maven build image.

**Why it matters:** A floating base image can change between builds, reducing reproducibility and potentially introducing unexpected dependency or security changes.

### Issue: Single-stage container build

**Current state:** The Maven build environment is also used as the runtime image.

**Fix:** Use a multi-stage Docker build with a Maven/JDK build stage and a smaller Java runtime stage.

**Why it matters:** The production container does not need Maven or the JDK build tooling. Removing them reduces image size and attack surface.

### Issue: Entire repository copied into the image

**Current state:** `COPY . .`

**Fix:** Copy only the files required for the Maven build and resulting application artefact, with an appropriate `.dockerignore`.

**Why it matters:** Avoids unnecessarily including source-control metadata, local files and other build artefacts in the container image.

### Issue: Container runs with unnecessary privileges

**Current state:** No non-root runtime user is configured.

**Fix:** Create and use a dedicated non-root application user.

**Why it matters:** Follows the principle of least privilege and reduces the impact of a potential container compromise.

### Issue: Tests skipped during image build

**Current state:** Maven is invoked with `-DskipTests`.

**Fix:** Keep testing responsibility in the CI pipeline while ensuring the production image build consumes a verified application artefact.

**Why it matters:** Tests should be an explicit CI quality gate rather than silently being skipped without explanation.

---

## Maven / Application

### Issue: Spring Boot 2.7.18 is at the end of its open-source support lifecycle

**Current state:** The application uses Spring Boot 2.7.18.

**Fix:** Do not perform a major Spring Boot migration as part of this exercise. Record migration to a supported Spring Boot generation as future technical debt.

**Why it matters:** Spring Boot 2.7.18 is the final 2.7 open-source release. A future upgrade should be planned, but introducing a major framework migration during a focused DevOps exercise would increase scope and risk unnecessarily.

### Issue: Direct dependency on Log4j Core 2.14.1

**Current state:** `log4j-core` version `2.14.1` is explicitly declared.

**Fix:** Investigate whether the dependency is actually required. If it is not required by the application, remove the direct dependency and rely on the Spring Boot-managed logging stack. If Log4j is required, move to a supported secure version.

**Why it matters:** The supplied version is affected by known Log4j security vulnerabilities and should not remain as an unnecessary direct dependency.

### Issue: Coverage is generated but not currently enforced

**Current state:** JaCoCo generates a report but the Maven build does not enforce a minimum coverage threshold.

**Fix:** Add a JaCoCo coverage rule requiring at least 70% line coverage.

**Why it matters:** Coverage reporting provides visibility, while an enforced threshold creates an actual quality gate.

---

## Azure Pipelines

### Issue: Database password is hard-coded in source control

### Hard-coded database password

**Original issue:** `dbPassword` contained a plaintext password in `azure-pipelines.yml`.

**Remediation:** Removed the unused credential from the pipeline. No application or deployment step referenced the variable, so retaining it provided no functional value and created an unnecessary secret-management risk.

**Current state:** No database password is stored in the repository or pipeline YAML. If a database credential is required in a future deployment, it should be supplied through a protected secret mechanism such as Azure Key Vault or Azure DevOps secret variables rather than source control.

### Issue: Test failures do not fail the pipeline

**Current state:** The Maven test task uses `continueOnError: true`.

**Fix:** Remove `continueOnError` and ensure failed tests fail the build.

**Why it matters:** A CI pipeline must prevent known failing tests from progressing towards deployment.

### Issue: No coverage quality gate

**Current state:** The pipeline does not enforce minimum test coverage.

**Fix:** Enforce the JaCoCo threshold during the Maven build.

**Why it matters:** Prevents changes with insufficient test coverage from progressing through the pipeline.

### Issue: No container vulnerability scanning

**Current state:** The container is built and pushed without a vulnerability scanning stage.

**Fix:** Add a container image security scanning stage before the image is promoted for deployment.

**Why it matters:** Vulnerable dependencies or operating-system packages should be identified before deployment.

### Issue: Image uses only the `latest` tag

**Current state:** The pipeline pushes `ssp/menu-service:latest`.

**Fix:** Tag images with an immutable build identifier and deploy that specific image.

**Why it matters:** Immutable image references provide traceability and make deployments reproducible.

### Issue: No development deployment stage

**Current state:** The pipeline deploys directly to production.

**Fix:** Introduce separate development and production deployment stages.

**Why it matters:** Provides an environment in which the built artefact can be validated before production deployment.

### Issue: No production approval

**Current state:** Production deployment occurs automatically after the preceding step.

**Fix:** Add an Azure DevOps production environment with an appropriate approval/check.

**Why it matters:** Provides an explicit production change-control point.

### Issue: No post-deployment smoke test

**Current state:** No application-level validation is performed after deployment.

**Fix:** Add automated smoke tests for `/health` and `/menu/{unitId}` after deployment.

**Why it matters:** Confirms that the deployed application is actually responding correctly.

---

## Terraform

### Issue: ACR admin authentication is enabled

**Current state:** `admin_enabled = true`.

**Fix:** Disable ACR admin authentication and use the App Service managed identity with the `AcrPull` role.

**Why it matters:** Avoids long-lived registry credentials and follows Azure identity-based access control.

### Issue: HTTPS is not enforced

**Current state:** `https_only = false`.

**Fix:** Enable HTTPS-only access.

**Why it matters:** Prevents unencrypted HTTP access to the application.

### Issue: App Service has no managed identity

**Current state:** No managed identity is configured.

**Fix:** Enable a system-assigned managed identity and use it for ACR access.

**Why it matters:** Enables passwordless Azure resource authentication.

### Issue: No explicit ACR pull role assignment

**Current state:** The App Service identity does not have an `AcrPull` role assignment.

**Fix:** Grant the App Service managed identity the minimum required ACR pull permission.

**Why it matters:** Applies least-privilege access to the container registry.

### Issue: Container image is hard-coded to `latest`

**Current state:** Terraform deploys `ssp/menu-service:latest`.

**Fix:** Parameterise the image tag so deployments can reference an immutable build identifier.

**Why it matters:** Ensures infrastructure deployment corresponds to a known application build.

### Issue: No production monitoring/alerting

**Current state:** No HTTP 5xx alert is configured.

**Fix:** Add monitoring and an appropriate production 5xx alert.

**Why it matters:** Provides operational visibility and allows failures to be detected after deployment.
