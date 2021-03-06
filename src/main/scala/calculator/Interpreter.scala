package calculator

import io.Source
import Evaluator._
import annotation.tailrec

/**
 * User: sun-april
 * Date: 10-12-26
 * letter := [a-zA-Z]
 *
 * letters := letter+
 *
 * digit := [0-9]
 *
 * digits := digit+
 *
 * variable := letter (digits | letters)?
 *
 * definition := nonLazyVarDefine
 *            := lazyVarDefinition
 *
 * nonLazyVarDefine := 'var' variable '=' expression
 *
 * lazyVarDefinition : = 'lazy' nonLazyVarDefine
 *
 * number := digits ('.' digits)? ([Ee] [+-]? digits)?
 *
 * factor := number
 *        := variable
 *        := '(' expression ')'
 *
 * term := factor * factor
 *      := factor / factor
 *
 * expression := term + term
 *            := term - term
 *
 * statement := definition
 *           := expression
 *
 * lazyVarDefinition
 */

object Interpreter {

  val NON_EOF = '\\'

  val EOF: Char = '\u0003'

  val stdinReader = Source.stdin.bufferedReader()

  def process(line: String): Expression Either Exception = try {
    Left(new Parser(line).process())
  } catch {
    case s: Exception => Right(s)
  }

  val prefix = "> "

  @tailrec
  def interpreterLoop() {
    val buffer = new StringBuilder()
    var line = ""
    var scan = true
    print(prefix)
    while (scan) {
      line = stdinReader.readLine()
      if (!line.trim.isEmpty) {
        if (line.last == NON_EOF)
          buffer.append(line.take(line.length - 1))
        else {
          buffer.append(line).append(EOF)
          scan = false
        }
      }
    }
    process(buffer.toString()) match {
      case Left(t) => printf("get : %s \n= %s \n", t, evaluate(t))
      case Right(e: ExpressionError) =>
        val padding = for (i <- 2 to (prefix.length + e.position)) yield ' '
        println(e.getMessage)
        println(line)
        print(padding.mkString)
        println('^')
      case Right(other) =>
        println("what happened ? got non expression exception  ")
        other.printStackTrace()
        println()
    }
    buffer.clear()
    interpreterLoop()
  }


  def main(args: Array[String]) {

    interpreterLoop()

  }

}