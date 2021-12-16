package withyuu.tutorial.java11to17.textblock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TextBlockTest {

  @Test
  void simpleTextBlock() {
    String textBlock = """
        <html>
            <body>
                <p>Don't think about elephants</p>
            </body>
        </html>
        """;
    String expected = "<html>\n" +
                      "    <body>\n" +
                      "        <p>Don't think about elephants</p>\n" +
                      "    </body>\n" +
                      "</html>\n";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void noTrailingNewline() {
    String textBlock = """
        <html>
            <body>
                <p>It was you. You were my ghost.</p>
            </body>
        </html>""";
    String expected = "<html>\n" +
                      "    <body>\n" +
                      "        <p>It was you. You were my ghost.</p>\n" +
                      "    </body>\n" +
                      "</html>";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  @Disabled("This test case is just for documentation purpose")
  void illFormedTextBlock() {
    // String a = """""";   // missing new line after opening quotes
    // String b = """ """;  // missing new line after opening quotes
    // String c = """
    //       ";        // no closing delimiter (text block continues to EOF)
    // String d = """
    //            abc \ def
    //            """;      // unescaped backslash (see below for escape processing)
  }

  @Test
  void trailingWhiteSpacesInEachLineAreRemoved() {
    String textBlock = """
        <html>   
            <body>  
                <p>If you're good at something, never do it for free.</p>   
            </body>  
        </html>   
        """;
    String expected = "<html>\n" +
                      "    <body>\n" +
                      "        <p>If you're good at something, never do it for free.</p>\n" +
                      "    </body>\n" +
                      "</html>\n";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void movingTheClosingDelimiterToTheLeftAddsSpacesAtTheBeginningOfEachLine() {
    String textBlock = """
            <html>
                <body>
                    <p>
                        Memories can be distorted. They're just an interpretation,
                        they're not a record, and they're irrelevant if you have the facts.
                    </p>
                </body>
            </html>
        """;
    String expected = "    <html>\n" +
                      "        <body>\n" +
                      "            <p>\n" +
                      "                Memories can be distorted. They're just an interpretation,\n" +
                      "                they're not a record, and they're irrelevant if you have the facts.\n" +
                      "            </p>\n" +
                      "        </body>\n" +
                      "    </html>\n";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void movingTheClosingDelimiterToTheRightHasNoEffect() {
    String textBlock = """
            <html>
                <body>
                    <p>Wars are not won by evacuation.</p>
                </body>
            </html>
                """;
    String expected = "<html>\n" +
                      "    <body>\n" +
                      "        <p>Wars are not won by evacuation.</p>\n" +
                      "    </body>\n" +
                      "</html>\n";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void escapeSequencesAreInterpreted() {
    String textBlock = """
            We can still use \t as tab.
            And we can still use \n for a new line
            And we can escape \' and \" thought it is unnecessary.
            Because we can use ' and " directly.
            But if we want to use three double quotes we must escape one out of three.
            Like this \"""
            this "\""
            or this ""\"
            """;
    String expected = "We can still use \t as tab.\n" +
                      "And we can still use \n" +
                      " for a new line\n" +
                      "And we can escape \' and \" thought it is unnecessary.\n" +
                      "Because we can use ' and \" directly.\n" +
                      "But if we want to use three double quotes we must escape one out of three.\n" +
                      "Like this \"\"\"\n" +
                      "this \"\"\"\n" +
                      "or this \"\"\"\n";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void useBackslashToContinueInTheSameLine() {
    String textBlock = """
        We shall go on to the end. We shall fight in France, we shall fight on \
        the seas and oceans, we shall fight with growing confidence and growing \
        strength in the air. We shall defend our island, whatever the cost may be...\
        """;
    String expected = "We shall go on to the end. We shall fight in France, we shall fight on " +
        "the seas and oceans, we shall fight with growing confidence and growing " +
        "strength in the air. We shall defend our island, whatever the cost may be...";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void useBackslashSAsASingleSpaceToAvoidTrimming() {
    String textBlock = """
        A: How would you like to die?    \s
        B: Old.                          \s
        A: You chose the wrong profession.""";
    String expected = "A: How would you like to die?     \n" +
                      "B: Old.                           \n" +
                      "A: You chose the wrong profession.";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void backslashSShouldWorkInNormalStringLiterals() {
    assertThat("\s\s").isEqualTo("  ");
  }

  @Test
  void concatTextBlock() {
    String thingToSay = "Abracadabra";
    String textBlock = """
        A: Do you have anything to say?
        B: \
        """ + thingToSay + """
        .\
        """;
    String expected = "A: Do you have anything to say?\n" +
        "B: Abracadabra.";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void formatTextBlock() {
    String thingToSay = "Abracadabra";
    String textBlock = """
        A: Do you have anything to say?
        B: %s.\
        """.formatted(thingToSay);
    String expected = "A: Do you have anything to say?\n" +
                      "B: Abracadabra.";
    assertThat(textBlock).isEqualTo(expected);
  }

  @Test
  void bewareOfEscapeSequencesInTextYouHaveCopiedPasted() {
    String textBlock = """
        {
          "someKey": "some value that contains \n inside the text"
        }
        """;
    String expected = "{\n" +
                      "  \"someKey\": \"some value that contains \n" +
                      " inside the text\"\n" +
                      "}\n";
    assertThat(textBlock).isEqualTo(expected);
  }



}
