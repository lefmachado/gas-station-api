# 3. Adopt GitFlow, Conventional Commits, and Branch Rulesets

- **Status:** Accepted
- **Date:** 2026-11-18

## Context
As the codebase evolves and potentially scales to additional contributors, direct pushes to main branches and inconsistent commit histories hinder traceability and release management.

## Decision
Use `develop` as the active integration branch and `main` for production releases, enforce Pull Requests with automated CI (`build-and-test`) via GitHub Rulesets, and standardize commits with Conventional Commits and Keep a Changelog.

## Consequences
- Guarantees that broken builds never reach `develop` or `main`.
- Enables automated release note generation and clear onboarding for new developers.
