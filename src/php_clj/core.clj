;; Copyright (c) 2014, Paul Mucur (http://mudge.name)
;; Released under the Eclipse Public License:
;; http://www.eclipse.org/legal/epl-v10.html

(ns php_clj.core
  (:require [php_clj.reader :as r]
            [clojure.string :as s]
            [flatland.ordered.map :refer [ordered-map]]))

(declare reader->clj)
(declare clj->php)

(defn- expect-char [reader expected]
  (let [actual (r/read-char reader)]
    (assert (= actual expected)
            (str "Expected " expected " but got " actual))))

(defn- parse-int [reader]
  (expect-char reader \:)
  (-> reader (r/read-until \;) Integer.))

(defn- parse-double [reader]
  (expect-char reader \:)
  (-> reader (r/read-until \;) Double.))

(defn- parse-string [reader]
  (expect-char reader \:)
  (let [size (Integer. (r/read-until reader \:))
        s (do (expect-char reader \")
              (r/read-str reader size))]
    (expect-char reader \")
    (expect-char reader \;)
    s))

(defn- parse-boolean [reader]
  (expect-char reader \:)
  (= "1" (r/read-until reader \;)))

(defn- parse-null [reader]
  (expect-char reader \;)
  nil)

(defn- parse-array [reader]
  (expect-char reader \:)
  (let [n-keys (Integer. (r/read-until reader \:))
        arr (do (expect-char reader \{)
                (loop [acc (ordered-map) n n-keys]
                  (if (zero? n) acc
                      (recur (assoc acc (reader->clj reader) (reader->clj reader))
                             (dec n)))))]
    (expect-char reader \})
    arr))

(defn- one-dimensional-array? [coll]
  (loop [i 0 indices (keys coll)]
    (cond
      (nil? (seq indices)) true
      (= i (first indices)) (recur (inc i) (next indices))
      :else false)))

(defn- parse-vector [array]
  (if (one-dimensional-array? array) (-> array vals vec)
    array))

(defn- reader->clj [reader]
  (case (r/read-char reader)
    \i (parse-int reader)
    \d (parse-double reader)
    \s (parse-string reader)
    \b (parse-boolean reader)
    \N (parse-null reader)
    \a (parse-vector (parse-array reader))))

(defn php->clj
  "Converts serialized PHP into equivalent Clojure data structures.

  Note that one-dimensional PHP arrays (those with consecutive indices starting
  at 0 such as array(1, 2, 3)) will be converted to vectors while all others
  will be converted to ordered maps therefore preserving insertion order.

  Example: (php->clj \"a:2:{i:0;i:1;i:1;i:2;}\")"
  [php]
  (let [reader (r/buffered-input-stream php)]
    (reader->clj reader)))

(defn- encode-string [^String clj]
  (let [bytes (-> clj .getBytes count)]
    (str "s:" bytes ":\"" clj "\";")))

(defn- encode-float [clj]
  (str "d:" clj ";"))

(defn- encode-int [clj]
  (str "i:" clj ";"))

(defn- encode-map [clj]
  (str (reduce (fn [php keyval]
                 (str php
                      (clj->php (key keyval))
                      (clj->php (val keyval))))
               (str "a:" (count clj) ":{")
               clj)
       "}"))

(defn- encode-collection [clj]
  (str "a:"
       (count clj) ":{"
       (s/join (map-indexed #(str (clj->php %) (clj->php %2)) clj))
       "}"))

(defn clj->php
  "Converts Clojure data structures into serialized PHP.

  Example: (clj->php [1 2 3])"
  [clj]
  (cond
    (map? clj)      (encode-map clj)
    (coll? clj)     (encode-collection clj)
    (string? clj)   (encode-string clj)
    (float? clj)    (encode-float clj)
    (integer? clj)  (encode-int clj)
    (true? clj)     "b:1;"
    (false? clj)    "b:0;"
    (nil? clj)      "N;"))
