## What is this
A Maven plugin for customizing what gets deleted from the target during mvn clean

## How do I use it
Here's an example of what to put in your pom.xml.  This example preserves Maven-managed JRuby gems so that they behave more like other Maven-managed jars in that they don't get wiped out by mvn clean:

    <plugin>
        <artifactId>maven-clean-plugin</artifactId>
        <version>2.6.1</version>
        <configuration>
          <filesets>
            <fileset>
              <directory>target</directory>
              <excludes>
                <exclude>rubygems</exclude>
                <exclude>test-classes</exclude>
              </excludes>
            </fileset>
          </filesets>
            <skip>true</skip>
        </configuration>
    </plugin>

    <plugin>
        <groupId>northpointdigital</groupId>
        <artifactId>selectiveclean-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <preserveInTarget>
              <param>rubygems</param>
              <param>test-classes</param>
            </preserveInTarget>
        </configuration>
        <executions>
          <execution>
            <phase>clean</phase>
            <goals>
              <goal>preservegems</goal>
            </goals>
          </execution>
        </executions>
    </plugin>


## What does that pom.xml config mean exactly?
The first plugin config simply disables the default "clean" behavior.
Then the next plugin does our work.  The important part is the preserveInTarget section, where we define which paths, relative to the build target, we wish to preserve during mvn clean
