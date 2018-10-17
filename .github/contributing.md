# Building a new version
In my setup I configured IntelliJ to generate the correct folder structure using Build->Build Artifacts.
The only manual step before publishing is to rename `library.properties` to `pLaunchController.txt`

# Publishing a new version
Publishing a new version to Processing library repository requires a Github release tagged with `latest`.
One can simply navigate to the release and re-upload the files generated above (pLaunchController.zip 
and pLaunchController.txt). Processing scrapper will check for updates from the pLaunchController.txt file.

# Adjusting the release tag to the most recent commit

Delete the tag on any remote before you push:

`git push origin :refs/tags/latest`

Replace the tag to reference the most recent commit:

`git tag -fa latest` -m "Updated tag to most recent commit"`

Push the tag to the remote origin:

`git push origin master --tags`