;; Copyright (c) 2014, Paul Mucur (http://mudge.name)
;; Released under the Eclipse Public License:
;; http://www.eclipse.org/legal/epl-v10.html

(ns php_clj.t-encoding
  (:require [php_clj.core :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(declare php-array)
(declare php-value)
(declare php-map)

(def php-value
  (gen/one-of [gen/string gen/int gen/boolean (gen/return nil)]))

(defn php-map
  [size]
  (if (zero? size)
    (gen/return {})
    (let [new-size (quot size 2)
          smaller-map (gen/resize new-size (gen/sized php-map))
          smaller-array (gen/resize new-size (gen/sized php-array))]
      (gen/map php-value (gen/one-of [php-value smaller-map])))))

(defn php-array
  [size]
  (if (zero? size)
    (gen/return [])
    (let [new-size (quot size 2)
          smaller-array (gen/resize new-size (gen/sized php-array))
          smaller-map (gen/resize new-size (gen/sized php-map))]
      (gen/vector (gen/one-of [php-value smaller-array])))))

(def php-type
  (gen/one-of [php-value (gen/sized php-map) (gen/sized php-array)]))

(defspec converting-to-php-twice-should-match-once
  (prop/for-all [v php-type]
                (= (clj->php v)
                   (clj->php (php->clj (clj->php v))))))
