/*
 *  Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License.
 *     A copy of the License is located at
 *
 *         http://aws.amazon.com/apache2.0/
 *
 *     or in the "license" file accompanying this file.
 *     This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and limitations under the License.
 *
 *  Portions copyright Copyright 2002-2016 JUnit. All Rights Reserved.
 *  Please see LICENSE.txt for applicable license terms and NOTICE.txt for applicable notices.
 */

import com.amazonaws.services.codebuild.model.EnvironmentVariable;
import com.amazonaws.services.codebuild.model.InvalidInputException;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodeBuilderHelperTest extends CodeBuilderTest {

    @Test
    public void TestGenerateS3URLNull() throws Exception {
        CodeBuilder cb = createDefaultCodeBuilder();
        assert(cb.generateS3ArtifactURL(null, null, null).isEmpty());
    }

    @Test
    public void TestGenerateS3URLEmpty() throws Exception {
        CodeBuilder cb = createDefaultCodeBuilder();
        assert(cb.generateS3ArtifactURL("", "", "").isEmpty());
    }

    @Test
    public void TestGenerateS3URL() throws Exception {
        String baseURL = "https://url.com/";
        String location = "bucket1";
        String type = "S3";
        CodeBuilder cb = createDefaultCodeBuilder();
        String result = cb.generateS3ArtifactURL(baseURL, location, type);
        assert(result.equals(baseURL + location));
    }

    @Test
    public void TestMapEnvVarsEmpty() throws InvalidInputException {
        String evs = "";
        CodeBuilder.mapEnvVariables(evs);
    }

    @Test
    public void TestMapEnvVarsNull() throws InvalidInputException {
        CodeBuilder.mapEnvVariables(null);
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsEmptyBrackets() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsNestedEmptyBrackets() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsNestedEmptyBracketsWithComma() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{,}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsSingleNameEmpty() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{,value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsSingleValueEmpty() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name,}]");
    }

    @Test
    public void TestMapEnvVarsSingle() throws InvalidInputException {
        Collection<EnvironmentVariable> result = CodeBuilder.mapEnvVariables("[{name, value}]");
        assert(result.size() == 1);
        List<EnvironmentVariable> evs = new ArrayList<>(result);
        assert(evs.get(0).getName().equals("name"));
        assert(evs.get(0).getValue().equals("value"));
    }

    @Test
    public void TestMapEnvVarsSingleWithWhitespace() throws InvalidInputException {
        Collection<EnvironmentVariable> result = CodeBuilder.mapEnvVariables("  [{   name, value \n} \t] ");
        assert(result.size() == 1);
        List<EnvironmentVariable> evs = new ArrayList<>(result);
        assert(evs.get(0).getName().equals("name"));
        assert(evs.get(0).getValue().equals("value"));
    }

    @Test
    public void TestMapEnvVarsTwo() throws InvalidInputException {
        Collection<EnvironmentVariable> result = CodeBuilder.mapEnvVariables("[{name, value}, {name2, value2}]");
        EnvironmentVariable ev1 = new EnvironmentVariable().withName("name").withValue("value");
        EnvironmentVariable ev2 = new EnvironmentVariable().withName("name2").withValue("value2");
        assert(result.size() == 2);
        assert(result.contains(ev1));
        assert(result.contains(ev2));
    }

    @Test
    public void TestMapEnvVarsMultiple() throws InvalidInputException {
        Collection<EnvironmentVariable> result =
                CodeBuilder.mapEnvVariables("[{name, value}, {name2, value2}, {key, val}, {k2, v2}]");
        EnvironmentVariable ev1 = new EnvironmentVariable().withName("name").withValue("value");
        EnvironmentVariable ev2 = new EnvironmentVariable().withName("name2").withValue("value2");
        EnvironmentVariable ev3 = new EnvironmentVariable().withName("key").withValue("val");
        EnvironmentVariable ev4 = new EnvironmentVariable().withName("k2").withValue("v2");
        assert(result.size() == 4);
        assert(result.contains(ev1));
        assert(result.contains(ev2));
        assert(result.contains(ev3));
        assert(result.contains(ev4));
    }

    @Test
    public void TestMapEnvVarsMultipleWhitespace() throws InvalidInputException {
        Collection<EnvironmentVariable> result =
                CodeBuilder.mapEnvVariables("\n [{ name   , value}, { name2\t, value2}, {  key, val},  {k2, v2 }]");
        EnvironmentVariable ev1 = new EnvironmentVariable().withName("name").withValue("value");
        EnvironmentVariable ev2 = new EnvironmentVariable().withName("name2").withValue("value2");
        EnvironmentVariable ev3 = new EnvironmentVariable().withName("key").withValue("val");
        EnvironmentVariable ev4 = new EnvironmentVariable().withName("k2").withValue("v2");
        assert(result.size() == 4);
        assert(result.contains(ev1));
        assert(result.contains(ev2));
        assert(result.contains(ev3));
        assert(result.contains(ev4));
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value}, bad{name2, value2}, {key, val}, {k2, v2}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid2() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[name, value]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid3() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[name{name,, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid4() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, anem, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid5() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, {name, value}, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid8() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value} name, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid6() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value} {name, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid7() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value},,{name, value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid9() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value},{name, value, }]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid10() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value},}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid11() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, value,}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid12() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{name, ,value}]");
    }

    @Test(expected=InvalidInputException.class)
    public void TestMapEnvVarsInvalid13() throws InvalidInputException {
        CodeBuilder.mapEnvVariables("[{,name value,}]");
    }

}
