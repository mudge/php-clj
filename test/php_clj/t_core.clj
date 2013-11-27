(ns php_clj.t-core
  (:use midje.sweet)
  (:use [php_clj.core]))

(facts "about `php->clj`"
  (fact "it converts integers"
    (php->clj "i:1;") => 1
    (php->clj "i:123;") => 123)
  (fact "it converts floats"
    (php->clj "d:12.300000000000001;") => (float 12.3)
    (php->clj "d:1;") => (float 1.0))
  (fact "it converts strings"
    (php->clj "s:12:\"Hello world!\";") => "Hello world!"
    (php->clj "s:6:\"Café!\";") => "Café!")
  (fact "it converts booleans"
    (php->clj "b:0;") => false
    (php->clj "b:1;") => true)
  (fact "it converts null"
    (php->clj "N;") => nil))
