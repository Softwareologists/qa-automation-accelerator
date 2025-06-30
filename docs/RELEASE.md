# Release Process

This project publishes a GitHub release whenever a new git tag is pushed.
Tags should follow semantic versioning, e.g. `v1.2.3`.
If the tag contains a suffix such as `-rc1` it will be marked as a pre-release.

## Steps

1. Update `CHANGELOG.md` with the new version entry.
2. Commit your changes and create an annotated tag:
   
   ```bash
   git tag -a vX.Y.Z -m "Release vX.Y.Z"
   git push origin vX.Y.Z
   ```
   
   Replace `vX.Y.Z` with your version. Add a suffix like `-beta1` for pre-releases.
3. Pushing the tag triggers the `release.yml` workflow which:
   - Builds all modules and runs tests and lint checks.
   - Creates application and agent binaries.
   - Generates release notes and uploads the artifacts as release assets.
4. Verify the GitHub release page for the uploaded binaries.

