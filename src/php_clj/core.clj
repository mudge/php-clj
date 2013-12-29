(ns php_clj.core
  (:require [php_clj.reader :as r]))

(declare reader->clj)

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
                (loop [acc {} n n-keys]
                  (if (zero? n) acc
                    (recur (assoc acc (reader->clj reader) (reader->clj reader))
                           (dec n)))))]
    (expect-char reader \})
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
