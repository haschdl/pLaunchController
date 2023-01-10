# Building a new version
In my setup I configured IntelliJ to generate the correct folder structure using Build->Build Artifacts.

There are 2 manual steps before publishing a new release.

1) Generated the documentation. In IntelliJ, go to `Tools` > `Generate Jadoc...`
2) After building the new artefacts, rename `library.properties` to `pLaunchControl.txt` in the output folder.

# Publishing a new version
Publishing a new version to Processing library repository requires a Github release tagged with `latest`.
One can simply navigate to the release and re-upload the files generated above (pLaunchControl.zip 
and pLaunchControl.txt). Processing scrapper will check for updates from the pLaunchControl.txt file.

# Adjusting the release tag to the most recent commit

Delete the tag on any remote before you push:

`git push origin :refs/tags/latest`

Replace the tag to reference the most recent commit:

`git tag -fa latest` -m "Updated tag to most recent commit"`

Push the tag to the remote origin:

`git push origin master --tags`