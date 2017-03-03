[![Build Status](https://api.travis-ci.org/racodond/sonar-jproperties-custom-rules-plugin.svg?branch=master)](https://travis-ci.org/racodond/sonar-jproperties-custom-rules-plugin)
[![AppVeyor Build Status](https://ci.appveyor.com/api/projects/status/wde8h8q7dchsq0ne/branch/master?svg=true)](https://ci.appveyor.com/project/racodond/sonar-jproperties-custom-rules-plugin/branch/master)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=org.sonar.sonar-plugins:sonar-jproperties-custom-rules-plugin)](https://sonarqube.com/dashboard?id=org.sonar.sonar-plugins%3Asonar-jproperties-custom-rules-plugin)

# Sample plugin that defines SonarQube custom rules for Java Properties files

## Description
The [SonarQube Java Properties plugin](https://github.com/racodond/sonar-jproperties-plugin) can be enhanced by writing custom rules through a plugin using SonarQube Java Properties API.
This sample plugin is designed to help you get started writing your own plugin and custom rules.

## Usage
1. [Download and install](http://docs.sonarqube.org/display/SONAR/Setup+and+Upgrade) SonarQube 5.6 or greater
1. Install the Java Properties plugin (2.1 or greater) either by a [direct download](https://github.com/racodond/sonar-jproperties-plugin/releases) or through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center).
1. Install this sample plugin by a [direct download](https://github.com/racodond/sonar-jproperties-custom-rules-plugin/releases)
1. Start SonarQube
1. [Activate some of the custom rules](http://docs.sonarqube.org/display/SONAR/Configuring+Rules) implemented in this sample plugin. "Forbidden keys should not be used" for example.
1. [Install your favorite analyzer](http://docs.sonarqube.org/display/SONAR/Analyzing+Source+Code#AnalyzingSourceCode-RunningAnalysis) (SonarQube Scanner, Maven, Ant, etc.) and analyze your code. Note that Java 8 is required to run an analysis.
1. Browse the issues through the web interface 

## Writing Custom Rules

### Creating a SonarQube Plugin
* Create a [standard SonarQube plugin](http://docs.sonarqube.org/display/DEV/Build+Plugin) from scratch or start from this sample plugin
* Attach this plugin to the SonarQube Java Properties plugin through the [POM](pom.xml):
  * Add the [dependency](pom.xml#L71) to the Java Properties plugin
  * Add the following property to the [`sonar-packaging-maven-plugin` configuration](pom.xml#L105):
 ```
 <basePlugin>javaProperties</basePlugin>
 ```
* Implement the following extension points:
  * [Plugin](http://javadocs.sonarsource.org/latest/apidocs/index.html?org/sonar/api/Plugin.html) as in [`MyJavaPropertiesCustomRulesPlugin.java`](src/main/java/org/sonar/jproperties/MyJavaPropertiesCustomRulesPlugin.java)
  * [RulesDefinition](http://javadocs.sonarsource.org/latest/apidocs/index.html?org/sonar/api/server/rule/RulesDefinition.html) as in [`MyJavaPropertiesCustomRulesDefinition.java`](src/main/java/org/sonar/jproperties/MyJavaPropertiesCustomRulesDefinition.java)
* Declare the [`RulesDefinition` implementation as an extension in the `Plugin` extension point](src/main/java/org/sonar/jproperties/MyJavaPropertiesCustomRulesPlugin.java#L34).

### Implementing a Rule
* Create a class to define the implementation of a rule. It should:
  * Either extend [`SubscriptionVisitorCheck`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/visitors/SubscriptionVisitorCheck.java) or [`DoubleDispatchVisitorCheck`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/visitors/DoubleDispatchVisitorCheck.java).
  * Define the [rule's attributes](src/main/java/org/sonar/jproperties/checks/ForbiddenKeysCheck.java#L32): key, name, priority, etc.
* Declare this class in the [class implementing `RulesDefinition`](src/main/java/org/sonar/jproperties/MyJavaPropertiesCustomRulesDefinition.java#L51)

There are two different ways to browse the AST:

#### Using DoubleDispatchVisitorCheck
To explore part of the AST, override a method from [`DoubleDispactchVisitor`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/visitors/DoubleDispatchVisitor.java).
For instance, if you want to explore key nodes, override [`DoubleDispactchVisitor#visitKey`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/visitors/DoubleDispatchVisitor.java#L78). This method is called each time a key node is encountered in the AST.
Note: When overriding a visit method, you must call the super method in order to allow the visitor to visit the children of the node.
See [`ForbiddenKeysCheck`](src/main/java/org/sonar/jproperties/checks/ForbiddenKeysCheck.java) for example.


#### Using SubscriptionVisitorCheck
To explore part of the AST, override [`SubscriptionVisitor#nodesToVisit`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/visitors/SubscriptionVisitor.java#L36) by returning the list of [`Tree#Kind`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/tree/Tree.java#L31) nodes you want to visit.
For instance, if you want to explore key nodes the method should return a list containing [`Tree#Kind#KEY`](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/plugins/jproperties/api/tree/Tree.java#L35).
See [`ForbiddenStringCheck`](src/main/java/org/sonar/jproperties/checks/ForbiddenStringCheck.java) for example.

#### Creating Issues
Precise issue or file issue or line issue can be created by calling the related method in [Issues](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-frontend/src/main/java/org/sonar/jproperties/visitors/Issues.java).

#### Testing
Testing is made easy by the [JavaPropertiesCheckVerifier](https://github.com/racodond/sonar-jproperties-plugin/blob/master/jproperties-checks-testkit/src/main/java/org/sonar/jproperties/checks/verifier/JavaPropertiesCheckVerifier.java).
There are two ways to assert that an issue should be raised:
* Through comments directly in the .properties test file
* Or using assertions in the check class test

Examples of coding rule implementation and testing can be found in the Java Properties plugin [`jproperties-checks` module](https://github.com/racodond/sonar-jproperties-plugin/tree/master/jproperties-checks/src/main/java/org/sonar/jproperties/checks).
