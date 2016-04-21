package net.stoerr.grokconstructor.patterntranslation

import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

import net.stoerr.grokconstructor.RandomTryLibrary
import net.stoerr.grokconstructor.automatic.AutomaticDiscoveryView
import net.stoerr.grokconstructor.matcher.MatcherEntryView
import net.stoerr.grokconstructor.webframework.{WebView, WebViewWithHeaderAndSidebox}

import scala.util.{Failure, Random, Success, Try}
import scala.xml.NodeSeq

/**
  * Created by hps on 13.04.2016.
  */
class PatternTranslatorView(val request: HttpServletRequest) extends WebViewWithHeaderAndSidebox {

  private val logger = Logger.getLogger("PatternTranslatorView")

  override val title: String = "Pattern Translation"
  val form = PatternTranslatorForm(request)

  override def action = PatternTranslatorView.path

  override def maintext: NodeSeq = <p>This tries to generate a
    <a href="http://logstash.net/docs/latest/filters/grok">grok regular expression</a>
    from a log4j
    <a href="https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html">PatternLayout</a>
    format that parses the logfile output generated by that format.
    You will want to check and refine the pattern with the
    <a href={fullpath(MatcherEntryView.path)}>matcher</a>
    .
  </p> ++ <p>
    This is currently very experimental - don't expect it to work or anything. :-)
    Please report problems and, if possible, make good
    suggestions how to translate troublesome placeholders to a appropriate grok expressions.</p> ++
    <p>It would be comparatively easy to extend this to other logging libraries like logback etc.
      if someone comes up with good suggestions how to translate the different placeholders.</p> ++
    <p>Please enter the log4j pattern and then press</p> ++ submit("Go!")

  val translationResult: Option[Try[String]] = form.format.value.map(pat => Try(Log4jTranslator.translate(pat)))

  override def sidebox: NodeSeq = <p>You can also just fill this with a</p> ++ buttonanchor(PatternTranslatorView.path + "?randomize", "random example.") ++
    (if (form.format.value.isEmpty || translationResult.isEmpty || translationResult.get.isFailure) <span/>
    else <p>You can also try out the constructed regex by calling the matcher.</p> ++ submit("Go to matcher", "matcher"))

  val examples = List("%d{dd.MM.yyyy HH:mm:ss},%m%n",
    "%-4r [%t] %-5p %c %x - %m%n",
    "%d{yyyyMMddHHmmss};%X{host};COMMONS;(%13F:%L);%X{gsid};%X{lsid};%-5p;%m%n",
    "[cc]%d{MMM-dd HH:mm:ss} %-14.14c{1}- %m%n",
    "%d{ABSOLUTE} | %-5p | %-10t | %-24.24c{1} | %-30.30C %4L | %m%n",
    "%d{dd.MM.yyyy HH:mm:ss,SSS} - %r [%-5p] %c %m%n",
    "%d{yyyy-MM-dd HH:mm:ss} %-5.5p [%-30c{1}] %-32X{sessionId} %X{requestId} - %m\n"
  )

  override def doforward: Option[Either[String, WebView]] =
    if (null != request.getParameter("randomize")) Some(Left(fullpath(PatternTranslatorView.path) + "?example=" + Random.nextInt(examples.size)))
    else if (null != request.getParameter("matcher")) {
      val view = new MatcherEntryView(request)
      view.form.pattern.value = Some(translationResult.get.get)
      Some(Right(view))
    } else None

  if (null != request.getParameter("example")) {
    val trial = examples(request.getParameter("example").toInt)
    form.format.value = Some(trial)
  }

  def formparts: NodeSeq = form.patternEntry ++ resultPart

  override def result: NodeSeq = <span/>

  def resultPart = translationResult.map {
    case Success(translated) =>
      form.result.value = Some(translated)
      table(row(form.result.inputTextArea("Constructed grok pattern", 180, 6, enabled = false)))
    case Failure(TranslationException(message)) =>
      table(warn(s"The pattern could not be translated because : $message"))
    case Failure(otherException) => throw otherException
  }.getOrElse(<span/>)

}


object PatternTranslatorView {

  val path = "/translator"

}