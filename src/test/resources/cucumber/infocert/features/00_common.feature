
@test-common-stepdefs
Feature: Test dei stepdefs predefiniti

	Scenario: Scenario di test utilizzando CommonStepdef
		When I navigate to "http://www.miriade.it"
		And I wait 500 ms
		Then the title should contain "Miriade"

	@java8
	Scenario: Scenario di test utilizzando CommonJava8Stepdef
		When I navigate to "http://www.miriade.it" Java8
		And I wait 500 ms Java8
		Then the title should contain "Miriade" Java8

	@yaml
	Scenario: Scenario di test utilizzando CommonStepdef e lo YAML Support
		When I navigate to yaml:"url"
		And I wait 500 ms
		Then the title should contain yaml:"title"

	@yaml 
	@java8
	Scenario: Scenario di test utilizzando CommonJava8Stepdef e lo YAML Support
		When I navigate to yaml:"url" Java8
		And I wait 500 ms Java8
		Then the title should contain yaml:"title" Java8