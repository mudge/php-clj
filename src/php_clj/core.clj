(ns php_clj.core
  (:require [clojure.string :as s]))

(defn- parse-int [php] (Integer. php))

(defn- parse-double [php] (Double. php))

(defn- parse-string [php]
  (let [[size value] (s/split php #":" 2)
        string (subs value 1 (dec (count value)))]
    string))

(defn- parse-boolean [php]
  (if (= "0" php) false true))

(defn php->clj [php]
  (let [l (count php)
        t (subs php 0 2)
        v (when (> l 2) (subs php 2 (dec l)))]
    (case t
      "i:" (parse-int v)
      "d:" (parse-double v)
      "s:" (parse-string v)
      "b:" (parse-boolean v)
      "N;" nil)))

