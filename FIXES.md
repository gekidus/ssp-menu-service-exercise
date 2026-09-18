# Fixes

List every problem you found in the starter files. For each one, note
the file, the fix, and why it matters for a service running across
3,000 outlets.


## Problem 1

File: Dockerfile  
Fix: Convert to a multi-stage build: use a Maven image to build the JAR in stage 1, then copy only the JAR into a slim JRE runtime image in stage 2.  
Why it matters: At 3,000 outlets, every MB of image size affects pull time, deployment speed, and storage costs. A slim runtime image also reduces the attack surface and number of vulnerabilities.


## Problem 2

File: Dockerfile  
Fix: Add a non-root user and run the Java process as that user (e.g. `appuser`).  
Why it matters: Running as root in containers is a security risk. If an attacker compromises the app, they have more power inside the container. At scale, this increases blast radius across thousands of nodes.


## Problem 3

File: Dockerfile  
Fix: Pin specific image tags instead of using `maven:latest` and implicit Java versions.  
Why it matters: `latest` can change unexpectedly, breaking builds or introducing vulnerabilities. For 3,000 outlets, reproducible builds and predictable behaviour are critical for reliability and auditing.


## Problem 4

File: Dockerfile  
Fix: Use the correct JAR name and path (confirm `target/menu-service.jar` matches the actual artifact) and ensure only the built JAR is copied into the runtime image.  
Why it matters: Hard-coded or incorrect paths cause runtime failures. At scale, inconsistent images lead to hard-to-debug issues and longer incident resolution times.


## Problem 5

File: azure-pipelines.yml  
Fix: Remove the hardcoded secret `dbPassword: 'P@ssw0rd123'` from the pipeline and use Azure Key Vault or pipeline secret variables instead.  
Why it matters: Hardcoded credentials in YAML are a major security risk. With many outlets and environments, leaked secrets could compromise multiple systems and regions.


## Problem 6

File: azure-pipelines.yml  
Fix: Do not set `continueOnError: true` on the Maven test step; instead, publish test results and fail the build on test failures.  
Why it matters: Silently ignoring test failures allows broken or regressions to reach production. Across 3,000 outlets, this could cause widespread POS failures during peak trading.


## Problem 7

File: azure-pipelines.yml  
Fix: Add explicit test result publication (PublishTestResults) and code coverage reporting, plus a gate to fail the build if coverage is below 70%.  
Why it matters: Without visibility into test health, quality degrades over time. At scale, this increases the risk of outages and makes it harder to detect risky changes early.


## Problem 8

File: azure-pipelines.yml  
Fix: Add a container image vulnerability scanning step before pushing to the registry.  
Why it matters: Vulnerable base images or dependencies can be exploited. With thousands of outlets, a single vulnerable image can be replicated widely, magnifying risk.


## Problem 9

File: azure-pipelines.yml  
Fix: Introduce separate stages for dev and prod, use Azure DevOps environments, and require manual approval for prod deployments.  
Why it matters: Direct deploys to prod on every commit are risky. For a global estate, you need change control and the ability to validate in dev before rolling out broadly.


## Problem 10

File: azure-pipelines.yml  
Fix: Add a time-based guardrail to block production deployments between 06:00 and 10:00 UK time.  
Why it matters: The business rule exists to protect the breakfast peak. Ignoring this could cause outages or performance issues during the busiest trading window across UK outlets.


## Problem 11

File: azure-pipelines.yml  
Fix: Add post-deployment smoke tests that call `/health` and `/menu/{unitId}` with retries, and fail the pipeline if they don’t pass.  
Why it matters: A deployment that appears successful but returns errors is worse than a failed deployment. At scale, you need automated validation to catch bad releases before they affect many sites.


## Problem 12

File: azure-pipelines.yml  
Fix: Remove hardcoded values (registry name, app name, etc.) and use variables / variable groups so dev and prod can differ cleanly.  
Why it matters: Hardcoding makes multi-environment and multi-country scaling error-prone. For 3,000 outlets across many regions, you need consistent, parameterised pipelines.


## Problem 13

File: infra/main.tf  
Fix: Add missing resources: Key Vault, Application Insights, and Log Analytics workspace.  
Why it matters: Without Key Vault, secrets are not managed securely. Without Application Insights/Log Analytics, you lack observability. At scale, this makes incidents harder to detect and resolve.


## Problem 14

File: infra/main.tf  
Fix: Disable `admin_enabled` on the Container Registry and use managed identity / RBAC (`AcrPull`) for the App Service to pull images.  
Why it matters: Admin credentials on ACR are a security risk. With many environments and countries, you want least-privilege access and no shared admin passwords.


## Problem 15

File: infra/main.tf  
Fix: Set `https_only = true` on the App Service.  
Why it matters: Allowing HTTP exposes traffic to interception. For POS systems handling pricing and potentially sensitive data, TLS everywhere is essential.


## Problem 16

File: infra/main.tf  
Fix: Introduce proper variable usage and separate `.tfvars` files for dev and prod (different SKUs, names, etc.), and ensure the configuration supports multiple environments cleanly.  
Why it matters: A single hardcoded config doesn’t scale. For 38 countries and many environments, you need reusable, parameterised Terraform with clear dev/prod separation.


## Problem 17

File: infra/main.tf  
Fix: Configure the App Service to use the ACR login server correctly with managed identity, and wire Application Insights connection string into app settings.  
Why it matters: Without proper integration, the app can’t pull images securely or emit telemetry. At scale, missing telemetry blinds you to issues affecting many outlets.


## Problem 18

File: infra/main.tf  
Fix: Add monitoring alerts (e.g., HTTP 5xx > 10 in 5 minutes) and action groups.  
Why it matters: You need proactive alerting to detect and respond to incidents before they impact large numbers of sites and customers.