;; Copyright (c) 2014, Paul Mucur (http://mudge.name)
;; Released under the Eclipse Public License:
;; http://www.eclipse.org/legal/epl-v10.html

(ns php_clj.t-reader
  (:require [php_clj.reader :as r]
            [midje.sweet :refer :all]))

(def input (r/buffered-input-stream "s:2:\"é\";"))

(fact "it can read a single character from a stream"
      (r/read-char input) => \s
      (r/read-char input) => \:
      (r/read-char input) => \2
      (r/read-char input) => \:
      (r/read-char input) => \")

(fact "it can read multiple bytes from a stream as a string"
      (r/read-str input 2) => "é")

(fact "it can resume reading single characters"
      (r/read-char input) => \"
      (r/read-char input) => \;)

(def input2 (r/buffered-input-stream "i:123;"))

(fact "it can read until a delimiter"
      (r/read-until input2 \:) => "i"
      (r/read-until input2 \;) => "123")
