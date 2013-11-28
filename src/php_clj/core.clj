(ns php_clj.core
  (:require [clojure.string :as s]))

(declare php->clj)

(defn- parse-int [php] (Integer. php))

(defn- parse-double [php] (Double. php))

(defn- parse-string [php]
  (let [[size value] (s/split php #":" 2)
        string (subs value 1 (dec (count value)))]
    string))

(defn- parse-boolean [php]
  (= "1" php))

(defn- parse-array [php]
  (let [[size value] (s/split php #":" 2)
        array (subs value 1 (dec (count value)))
        values (s/split array #"(?<=;)")]
    (apply hash-map (map php->clj values))))

(defn- extract-value [php]
  (let [t (subs php 0 2)
        l (count php)]
    (case t
      "N;" nil
      "a:" (subs php 2)
      (subs php 2 (dec l)))))

(defn php->clj [php]
  (let [t (subs php 0 2)
        v (extract-value php)]
    (case t
      "i:" (parse-int v)
      "d:" (parse-double v)
      "s:" (parse-string v)
      "b:" (parse-boolean v)
      "a:" (parse-array v)
      "N;" nil)))

