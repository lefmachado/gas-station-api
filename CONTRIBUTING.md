# Contributing to Gas Station API

Thank you for contributing! Follow these guidelines to keep the codebase consistent and easy to maintain.

## Prerequisites
- **Java 25+**
- **Docker** and **Docker Compose**

## GitFlow & Branch Naming
Always branch off from `develop` and target Pull Requests back to `develop`:
- `feat/<issue-number>-<short-description>` — New features
- `fix/<issue-number>-<short-description>` — Bug fixes
- `chore/<issue-number>-<short-description>` — Maintenance, docs, or CI updates

## Commit Convention
Follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` New functionality
- `fix:` Bug fix
- `docs:` Documentation changes
- `test:` Unit or integration tests
- `chore:` Build, CI, or repository maintenance

## Before Opening a Pull Request
1. Run `./mvnw clean verify` locally and ensure all tests pass.
2. Add a summary entry under `## [Unreleased]` in `CHANGELOG.md`.
3. Link the related issue in the PR description using `Closes #<issue-number>`.
