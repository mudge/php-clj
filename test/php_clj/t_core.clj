;; Copyright (c) 2014, Paul Mucur (http://mudge.name)
;; Released under the Eclipse Public License:
;; http://www.eclipse.org/legal/epl-v10.html

(ns php_clj.t-core
  (:require [flatland.ordered.map :refer [ordered-map]]
            [midje.sweet :refer :all]
            [php_clj.core :refer :all]))

(facts "about `php->clj`"
       (fact "it converts integers"
             (php->clj "i:1;") => 1
             (php->clj "i:123;") => 123
             (php->clj "i:-1;") => -1)
       (fact "it converts doubles"
             (php->clj "d:12.300000000000001;") => 12.3
             (php->clj "d:1;") => 1.0)
       (fact "it converts strings"
             (php->clj "s:12:\"Hello world!\";") => "Hello world!"
             (php->clj "s:6:\"Café!\";") => "Café!"
             (php->clj "s:3:\"♥\";") => "♥"
             (php->clj "s:10:\"Hey \"Bob!\"\";") => "Hey \"Bob!\""
             (php->clj "s:0:\"\";") => ""
             (php->clj "s:1:\";\";") => ";")
       (fact "it converts booleans"
             (php->clj "b:0;") => false
             (php->clj "b:1;") => true)
       (fact "it converts null"
             (php->clj "N;") => nil)
       (fact "it converts arrays"
             (php->clj "a:2:{i:2;i:3;i:4;i:6;}") => {2 3, 4 6}
             (php->clj "a:2:{s:4:\"name\";s:3:\"Bob\";s:3:\"age\";i:15;}") => {"name" "Bob", "age" 15}
             (php->clj "a:1:{s:7:\"\"Hey}{\"\";s:3:\"i:0\";}") => {"\"Hey}{\"" "i:0"}
             (php->clj "a:1:{s:1:\";\";s:3:\"}{;\";}") => {";" "}{;"})
       (fact "it converts single-dimensional arrays into vectors"
             (php->clj "a:3:{i:0;s:1:\"a\";i:1;s:1:\"b\";i:2;s:1:\"c\";}") => ["a" "b" "c"]
             (php->clj "a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}") => [1 2 3]
             (php->clj "a:2:{s:4:\"name\";s:3:\"Bob\";s:7:\"numbers\";a:2:{i:0;i:1;i:1;i:2;}}") => {"name" "Bob", "numbers" [1 2]})
       (fact "throws on invalid input"
             (php->clj "Not real PHP") => (throws #"Expected ; but got o")))

(facts "about `clj->php`"
       (fact "it converts nil"
             (clj->php nil) => "N;")
       (fact "it converts booleans"
             (clj->php true) => "b:1;"
             (clj->php false) => "b:0;")
       (fact "it converts integers"
             (clj->php 3) => "i:3;"
             (clj->php -2) => "i:-2;")
       (fact "it converts doubles"
             (clj->php 3.2) => "d:3.2;"
             (clj->php 1.0) => "d:1.0;")
       (fact "it converts strings"
             (clj->php "foo") => "s:3:\"foo\";"
             (clj->php "café") => "s:5:\"café\";")
       (fact "it converts maps"
             (clj->php {0 1, 1 2, 2 3}) => "a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}"
             (clj->php {"name" "Bob", "numbers" {0 1, 1 2}}) => "a:2:{s:7:\"numbers\";a:2:{i:0;i:1;i:1;i:2;}s:4:\"name\";s:3:\"Bob\";}")
       (fact "it converts ordered maps"
             (clj->php (ordered-map 0 1, 1 2, 2 3)) => "a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}"
             (clj->php (ordered-map "name" "Bob", "numbers" (ordered-map 0 1, 1 2))) => "a:2:{s:4:\"name\";s:3:\"Bob\";s:7:\"numbers\";a:2:{i:0;i:1;i:1;i:2;}}")
       (fact "it converts sequences"
             (clj->php [1 2 3]) => "a:3:{i:0;i:1;i:1;i:2;i:2;i:3;}"
             (clj->php [true false nil]) => "a:3:{i:0;b:1;i:1;b:0;i:2;N;}"
             (clj->php '(4 5 6)) => "a:3:{i:0;i:4;i:1;i:5;i:2;i:6;}"
             (clj->php #{9 10 11}) => "a:3:{i:0;i:9;i:1;i:10;i:2;i:11;}"))
