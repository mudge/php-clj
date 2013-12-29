(ns php_clj.core
  (:require [php_clj.reader :as r]))

(declare reader->clj)

(defn- parse-int [reader]
  (r/read-char reader) ;; Discard \:
  (-> reader (r/read-until \;) Integer.))

(defn- parse-double [reader]
  (r/read-char reader) ;; Discard \:
  (-> reader (r/read-until \;) Double.))

(defn- parse-string [reader]
  (r/read-char reader) ;; Discard \:
  (let [size (Integer. (r/read-until reader \:))
        s (do (r/read-char reader) ;; Discard \"
              (r/read-str reader size))]
    (r/read-char reader) ;; Discard \"
    (r/read-char reader) ;; Discard \;
    s))

(defn- parse-boolean [reader]
  (r/read-char reader) ;; Discard \:
  (= "1" (r/read-until reader \;)))

(defn- parse-null [reader]
  (r/read-char reader) ;; Discard \;
  nil)

(defn- parse-array [reader]
  (r/read-char reader) ;; Discard \:
  (let [n-keys (Integer. (r/read-until reader \:))
        arr (do (r/read-char reader) ;; Discard \{
                (loop [acc {} n n-keys]
                  (if (zero? n) acc
                    (recur (assoc acc (reader->clj reader) (reader->clj reader)) (dec n)))))]
    (r/read-char reader) ;; Discard \}
    arr))

(defn- reader->clj [reader]
  (case (r/read-char reader)
    \i (parse-int reader)
    \d (parse-double reader)
    \s (parse-string reader)
    \b (parse-boolean reader)
    \N (parse-null reader)
    \a (parse-array reader)))

(defn php->clj [php]
  (let [reader (r/buffered-input-stream php)]
    (reader->clj reader)))
