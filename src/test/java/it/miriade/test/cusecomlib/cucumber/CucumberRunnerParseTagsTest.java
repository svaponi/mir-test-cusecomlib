package it.miriade.test.cusecomlib.cucumber;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import it.miriade.test.cusecomlib.cucumber.CucumberRunner;

/**
 * Test del metodo {@link CucumberRunner#parseTags(String)}
 * 
 * @author svaponi
 */
public class CucumberRunnerParseTagsTest {

	@Test
	public void test() throws Throwable {
		int i = 1;

		/*
		 * OR) Running scenarios which match @important OR @billing
		 * cucumber --tags @billing,@important
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("@billing,@important"), CucumberRunner.parseTags("billing,important")); // 1
		Assert.assertEquals("test " + i++, Arrays.asList("@billing,~@important"), CucumberRunner.parseTags("billing,~important"));
		Assert.assertEquals("test " + i++, Arrays.asList("~@billing,@important"), CucumberRunner.parseTags("~billing,important"));
		Assert.assertEquals("test " + i++, Arrays.asList("~@billing,~@important"), CucumberRunner.parseTags("~billing,~important"));

		/*
		 * AND) Running scenarios which match @important AND @billing
		 * cucumber --tags @billing --tags @important
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("@billing", "@important"), CucumberRunner.parseTags("billing important")); // 4
		Assert.assertEquals("test " + i++, Arrays.asList("@billing", "~@important"), CucumberRunner.parseTags("billing ~important"));
		Assert.assertEquals("test " + i++, Arrays.asList("~@billing", "@important"), CucumberRunner.parseTags("~billing important"));
		Assert.assertEquals("test " + i++, Arrays.asList("~@billing", "~@important"), CucumberRunner.parseTags("~billing ~important"));

		/*
		 * Running scenarios which match: (@billing OR @wip) AND @important
		 * cucumber --tags @billing,@wip --tags @important
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("@billing,@wip", "@important"), CucumberRunner.parseTags("billing,wip important")); // 8

		/*
		 * Running scenarios which match: (@billing AND @wip) OR @important ==> NON è possibile!
		 * Biisogna scrivere in un altro modo: (@billing OR @important) AND (@wip OR @important)
		 * cucumber --tags @billing,@important --tags @wip,@important
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("@billing,@important", "@wip,@important"), CucumberRunner.parseTags("billing,important wip,important")); // 9

		/*
		 * Skipping both @todo and @wip tags
		 * cucumber --tags ~@todo --tags ~@wip
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("~@todo", "~@wip"), CucumberRunner.parseTags("~todo ~wip")); // 10

		/*
		 * Running scenarios which match: (NOT @billing OR @todo) AND (@important OR NOT @wip)
		 * cucumber --tags ~@billing,@todo --tags @important,~@wip
		 */
		Assert.assertEquals("test " + i++, Arrays.asList("~@billing,@todo", "@important,~@wip"), CucumberRunner.parseTags("~billing,todo important,~wip")); // 11
	}

	@Test
	public void test_con_chiocciola() throws Throwable {
		int i = 1;

		/*
		 * OR) Running scenarios which match @important OR @billing
		 * cucumber --tags @billing,@important
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing,@important"), CucumberRunner.parseTags("@billing,@important")); // 1
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing,~@important"), CucumberRunner.parseTags("@billing,~@important"));
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@billing,@important"), CucumberRunner.parseTags("~@billing,@important"));
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@billing,~@important"), CucumberRunner.parseTags("~@billing,~@important"));

		/*
		 * AND) Running scenarios which match @important AND @billing
		 * cucumber --tags @billing --tags @important
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing", "@important"), CucumberRunner.parseTags("@billing @important")); // 4
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing", "~@important"), CucumberRunner.parseTags("@billing ~@important"));
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@billing", "@important"), CucumberRunner.parseTags("~@billing @important"));
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@billing", "~@important"), CucumberRunner.parseTags("~@billing ~@important"));

		/*
		 * Running scenarios which match: (@billing OR @wip) AND @important
		 * cucumber --tags @billing,@wip --tags @important
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing,@wip", "@important"), CucumberRunner.parseTags("@billing,wip @important")); // 8

		/*
		 * Running scenarios which match: (@billing AND @wip) OR @important ==> NON è possibile!
		 * Biisogna scrivere in un altro modo: (@billing OR @important) AND (@wip OR @important)
		 * cucumber --tags @billing,@important --tags @wip,@important
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("@billing,@important", "@wip,@important"), CucumberRunner.parseTags("@billing,@important wip,@important")); // 9

		/*
		 * Skipping both @todo and @wip tags
		 * cucumber --tags ~@todo --tags ~@wip
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@todo", "~@wip"), CucumberRunner.parseTags("~todo ~wip")); // 10

		/*
		 * Running scenarios which match: (NOT @billing OR @todo) AND (@important OR NOT @wip)
		 * cucumber --tags ~@billing,@todo --tags @important,~@wip
		 */
		Assert.assertEquals("test " + i++ + " con @", Arrays.asList("~@billing,@todo", "@important,~@wip"), CucumberRunner.parseTags("~@billing,todo @important,~wip")); // 11
	}
}
