(ns php_clj.reader
  (:import [java.io ByteArrayInputStream BufferedInputStream]))

(defn buffered-input-stream [^String s]
  (-> s .getBytes ByteArrayInputStream. BufferedInputStream.))

(defn read-char [^BufferedInputStream stream]
  (-> stream .read char))

(defn read-str [^BufferedInputStream stream n]
  (let [selected-bytes (byte-array n)]
    (.read stream selected-bytes)
    (String. selected-bytes)))

(defn read-until [reader delimiter]
  (loop [acc []]
    (let [c (read-char reader)]
      (if (= delimiter c) (apply str acc)
        (recur (conj acc c))))))
