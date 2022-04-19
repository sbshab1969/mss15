1) JUL -
//  jdk8:  Настройки находятся в файле $java_home/jre/lib/logging.properties
//  jdk11: Настройки находятся в файле $java_home/conf/logging.properties

2) jboss logging - log4j, JUL
Logger SLF4J заменен на jbosslogging, который вызывает по умолчанию JDK logger (JUL)
При добавлении библиотеки log4j и файла конфигурации log4j.properties
логгирование переключается на log4j.
Можно настроить и логгер logback, но для этого нужен сам SLF4J.

либо log4j в pom
    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>

либо logback
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.5</version>
    </dependency>

иначе JUL

3) logback - logback.xml, BaseConfig
Если logback.xml не найден, то подключается базовая конфигурация по умолчанию.

4) Последняя работающая версия logback - 1.2.5
Начиная с 1.2.6 при запуске логгера возникает ошибка:

Parser configuration error occurred javax.xml.parsers.ParserConfigurationException: 
SAX feature 'http://xml.org/sax/features/external-general-entities' not supported.

    private SAXParser buildSaxParser() throws JoranException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            //spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setNamespaceAware(true);
            return spf.newSAXParser();
        } catch (Exception pce) {
            String errMsg = "Parser configuration error occurred";
            addError(errMsg, pce);
            throw new JoranException(errMsg, pce);
        }
    }

Ошибка возникает при использовании xmlparserv2.jar
    <dependency>
      <groupId>com.oracle.database.xml</groupId>
      <artifactId>xmlparserv2</artifactId>
      <version>${ojdbc.version}</version>
    </dependency>

До версии 1.2.6 все работало нормально.

