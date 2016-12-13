/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.polygene.gradle.doc

import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import java.security.MessageDigest
import org.apache.polygene.gradle.PolygeneExtension
import org.apache.polygene.gradle.release.ReleaseSpecExtension
import org.apache.polygene.gradle.tasks.ExecLogged
import org.gradle.api.Action;
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.process.ExecSpec

// TODO: try to use dependencies for FOP and execute within the same JVM.
// TODO: move the bulk of resources into this plugin, instead of sitting in the project.
@CompileStatic
class DocumentationTask extends DefaultTask
{
  @Input def String docName
  @Input def String docType

  @InputDirectory def File getCommonResourcesDir() { project.file( 'src/resources' ) }
  @InputDirectory def File getConfigDir() { project.file( 'src/conf' ) }
  @InputDirectory def File getDocsDir() { project.file( 'src/docs' ) }
  @InputDirectory def File getSrcMainDir() { project.file( 'src/main' ) }
  @InputDirectory def File getXslDir() { project.file( 'src/xsl' ) }
  @InputDirectory def File getBuildSrcDir() { project.rootProject.file( 'buildSrc/src' ) }

  @InputFiles def getSubProjectsDocsDirs() { project.rootProject.subprojects.collect { p -> p.file( 'src/docs' ) } }
  @InputFiles def getSubProjectsTestDirs() { project.rootProject.subprojects.collect { p -> p.file( 'src/test' ) } }

  @OutputDirectory def File getOutputDir() { project.file( "${ project.buildDir }/docs/${ docName }/" ) }

  @Internal def File getTempAsciidocDir() { project.file( "${ project.buildDir }/tmp-asciidoc" ) }
  @Internal def File getTempDir() { project.file( "${ project.buildDir }/tmp/docs/${ docName }" ) }

  @TaskAction
  def void generate()
  {
    installAsciidocFilters()

    [ outputDir, tempAsciidocDir, tempDir ].each { it.deleteDir() }
    [ outputDir, tempAsciidocDir, tempDir ].each { it.mkdirs() }

    copySubProjectsDocsResources()
    generateAsciidocAccordingToReleaseSpecification()
    generateXDoc()
    generateChunkedHtml()
    // generateSingleHtml()
    // generatePdf()
  }

  def void installAsciidocFilters()
  {
    def digester = MessageDigest.getInstance( 'SHA' )
    def filtersDir = project.rootProject.file( 'buildSrc/src/asciidoc/filters' )
    def userHome = new File( System.getProperty( 'user.home' ) )
    def dotAsciidocFiltersDir = new File( userHome, '.asciidoc/filters' )
    def installSnippets = false
    filtersDir.eachFileRecurse( FileType.FILES ) { originalFile ->
      def targetFile = new File( dotAsciidocFiltersDir,
                                 ( originalFile.toURI() as String ) - ( filtersDir.toURI() as String ) )
      if( !targetFile.exists() )
      {
        installSnippets = true
      }
      else
      {
        def originalDigest = digester.digest( originalFile.bytes )
        def targetDigest = digester.digest( targetFile.bytes )
        if( originalDigest != targetDigest )
        {
          installSnippets = true
        }
      }
    }
    if( installSnippets )
    {
      dotAsciidocFiltersDir.mkdirs()
      project.rootProject.copy { CopySpec spec ->
        spec.from filtersDir
        spec.into dotAsciidocFiltersDir
      }
      dotAsciidocFiltersDir.eachFileRecurse( FileType.FILES ) { file ->
        if( file.name.endsWith( '.py' ) )
        {
          chmod( file, '755' )
        }
      }
      println "Polygene Asciidoc Filters Installed!"
    }
  }

  @CompileStatic( TypeCheckingMode.SKIP )
  def void chmod( File file, String permissions )
  {
    ant.chmod( file: file.absolutePath, perm: permissions )
  }

  def void copySubProjectsDocsResources()
  {
    project.rootProject.subprojects.each { p ->
      p.copy { CopySpec spec ->
        spec.from p.file( 'src/docs/resources' )
        spec.into outputDir
        spec.include '**'
      }
    }
  }

  def void generateAsciidocAccordingToReleaseSpecification()
  {
    def polygene = project.extensions.getByType( PolygeneExtension )
    project.copy { CopySpec spec ->
      spec.from docsDir
      spec.into tempAsciidocDir
      spec.include '**'
    }
    if( polygene.releaseVersion )
    {
      def licenseFile = new File( tempAsciidocDir, 'userguide/libraries.txt' )
      def extensionsFile = new File( tempAsciidocDir, 'userguide/extensions.txt' )
      def toolsFile = new File( tempAsciidocDir, 'userguide/tools.txt' )
      [ licenseFile, extensionsFile, toolsFile ].each { asciidocFile ->
        def filteredFileContent = ''
        asciidocFile.readLines().each { line ->
          if( line.startsWith( 'include::' ) )
          {
            def approved = false
            def releaseApprovedProjects = project.rootProject.extensions.
              getByType( ReleaseSpecExtension ).approvedProjects
            releaseApprovedProjects.collect { it.projectDir }.each { approvedProjectDir ->
              if( line.contains( "${ approvedProjectDir.parentFile.name }/${ approvedProjectDir.name }" ) )
              {
                approved = true
              }
            }
            if( approved )
            {
              filteredFileContent += "$line\n"
            }
          }
          else
          {
            filteredFileContent += "$line\n"
          }
        }
        asciidocFile.text = filteredFileContent
      }
    }
  }

  def void generateXDoc()
  {
    def outLog = getLogFile( 'adoc-2-docbook', 'stdout' )
    def errLog = getLogFile( 'adoc-2-docbook', 'stderr' )
    ExecLogged.execLogged( project, outLog, errLog, { ExecSpec spec ->
      spec.executable = 'asciidoc'
      spec.workingDir = '..'
      def commonResourcesPath = relativePath( project.rootDir, commonResourcesDir )
      def asciidocConfigPath = relativePath( project.rootDir, new File( configDir, 'asciidoc.conf' ) )
      def docbookConfigPath = relativePath( project.rootDir, new File( configDir, 'docbook45.conf' ) )
      def linkimagesConfigPath = relativePath( project.rootDir, new File( configDir, 'linkedimages.conf' ) )
      def xdocOutputPath = relativePath( project.rootDir, new File( tempDir, 'xdoc-temp.xml' ) )
      def asciidocIndexPath = relativePath( project.rootDir, new File( tempAsciidocDir, "$docName/index.txt" ) )
      spec.args = [
        '--attribute', 'revnumber=' + project.version,
        '--attribute', 'level1=' + ( docType == 'article' ? 1 : 0 ),
        '--attribute', 'level2=' + ( docType == 'article' ? 2 : 1 ),
        '--attribute', 'level3=' + ( docType == 'article' ? 3 : 2 ),
        '--attribute', 'level4=' + ( docType == 'article' ? 4 : 3 ),
        '--attribute', 'importdir=' + commonResourcesPath,
        '--backend', 'docbook',
        '--attribute', 'docinfo1',
        '--doctype', docType,
        '--conf-file=' + asciidocConfigPath,
        '--conf-file=' + docbookConfigPath,
        '--conf-file=' + linkimagesConfigPath,
        '--out-file', xdocOutputPath,
        asciidocIndexPath
      ]
    } as Action<? super ExecSpec> )
  }

  def void generateChunkedHtml()
  {
    project.copy { CopySpec spec ->
      spec.from commonResourcesDir
      spec.into outputDir
      spec.include '**'
    }
    project.copy { CopySpec spec ->
      spec.from "$docsDir/$docName/resources"
      spec.into outputDir
      spec.include '**'
    }
    def outLog = getLogFile( 'docbook-2-chunked-html', 'stdout' )
    def errLog = getLogFile( 'docbook-2-chunked-html', 'stderr' )
    ExecLogged.execLogged( project, outLog, errLog, { ExecSpec spec ->
      def xsltFile = "$docsDir/$docName/xsl/chunked.xsl"
      def outputPath = relativePath( project.projectDir, outputDir ) + '/'
      spec.executable = 'xsltproc'
      spec.args = [
        '--nonet',
        '--noout',
        '--output', outputPath,
        xsltFile,
        "$tempDir/xdoc-temp.xml"
      ]
    } as Action<? super ExecSpec> )
  }

  def void generateSingleHtml()
  {
    def outLog = getLogFile( 'docbook-2-html', 'stdout' )
    def errLog = getLogFile( 'docbook-2-html', 'stderr' )
    ExecLogged.execLogged( project, outLog, errLog, { ExecSpec spec ->
      // XML_CATALOG_FILES=
      String xsltFile = "$xslDir/xhtml.xsl"
      spec.executable = 'xsltproc'
      spec.args = [
        '--nonet',
        '--noout',
        '--output', "$outputDir/${ docName }.html",
        xsltFile,
        "$tempDir/xdoc-temp.xml"
      ]
    } as Action<? super ExecSpec> )
  }

  def void generatePdf()
  {
    // $ xsltproc --nonet ../docbook-xsl/fo.xsl article.xml > article.fo
    def outLog = getLogFile( 'docbook-2-fo', 'stdout' )
    def errLog = getLogFile( 'docbook-2-fo', 'stderr' )
    ExecLogged.execLogged( project, outLog, errLog, { ExecSpec spec ->
      String xsltFile = "$xslDir/fo.xsl"
      spec.executable = 'xsltproc'
      spec.args = [
        '--nonet',
        '--output', "$tempDir/${ docName }.fo",
        xsltFile,
        "$tempDir/xdoc-temp.xml"
      ]
    } as Action<? super ExecSpec> )

    // $ fop article.fo article.pdf
    outLog = getLogFile( 'fo-2-pdf', 'stdout' )
    errLog = getLogFile( 'fo-2-pdf', 'stderr' )
    ExecLogged.execLogged( project, outLog, errLog, { ExecSpec spec ->
      spec.executable = 'fop'
      spec.args = [
        "$tempDir/${ docName }.fo",
        "$outputDir/${ docName }.pdf"
      ]
    } as Action<? super ExecSpec> )
  }

  private File getLogFile( String step, String stream )
  {
    return project.file( "${ project.buildDir }/tmp/${ name }/${ step }-${ stream }.log" )
  }

  private static String relativePath( File root, File target )
  {
    new File( root.toURI().relativize( target.toURI() ).toString() ).path
  }
}
