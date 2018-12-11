# CICO Shared Library
> A Jenkins Pipeline Shared Library for use in CentOS CI Jenkins Masters

## Installation
Ask your Jenkins administrator to add this as an implicitly loaded Global
Shared Library (`Manage Jenkins -> Configure System -> Global Pipeline
Libraries`)

### Prerequisites
This set of shared libraries is meant to work in conjunction with a set of
[Openshift Images](centosci/images). These need to be built and available for
pulling in all openshift namespaces.

## Usage

### Examples
##### The Fedora-tox runner:
This runner takes the project and runs tox for every released version of Fedora. 

1. Install the shared library into your jenkins master (if you're on a CentOS
CI master, this is already done for you)
2. Create a new Multibranch Pipeline job `New Item -> Multibranch Pipeline
Job`
3. Add a Github Branch Source `Branch Sources -> Add source` pointing at your
repository, be sure to use Github API credentials
4. Choose `.cico.pipeline` as the Build Configuration Script Path
5. Add a `.cico.pipeline` file in the root of your repository with the following contents:
   ```
   fedoraInfraTox {}
   ``` 

## Contributing
Pull-requests are welcome!

## License

[![License](http://img.shields.io/:license-mit-blue.svg?style=flat-square)](http://badges.mit-license.org)

- **[MIT license](http://opensource.org/licenses/mit-license.php)**
