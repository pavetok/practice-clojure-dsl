(ns practice-clojure-dsl.practices
  (:require [datomic.api :as d]
            [practice-clojure-dsl.values :refer :all]))

(defn fresh-database
  []
  (let [db-name (gensym)
        db-uri (str "datomic:mem://" db-name)]
    (d/create-database db-uri)
    (let [conn (d/connect db-uri)]
      conn)))

(def conn (fresh-database))

(defn reg
  [val]
  (register val conn))

(comment
  (let [; resource values
        rtv1 (reg {:val/label "T1"})
        rtv2 (reg {:val/label "T2"})
        rv1 (reg {:val/label "bar"})
        rv2 (reg {:val/label "baz"})
        ; resource types
        rt1 (reg {:val/eq [rtv1]
                  :val/sort :val/type.simple
                  :val/specs [rv1]})
        rt2 (reg {:val/eq [rtv2]
                  :val/sort :val/type.simple
                  :val/specs [rv2]})
        ; resource places
        rp1 (reg {:val/of rtv1})
        rp2 (reg {:val/of rtv2})
        ; competency values
        ctv1 (reg {:val/from rtv1
                   :val/to rtv2})
        cv1 (reg {:val/from rp1
                  :val/to rp2
                  :val/body "something invokable"})
        ; competency types
        ct1 (reg {:val/eq [ctv1]
                  :val/sort :val/type.fun
                  :val/specs [cv1]})]
    (check-in cv1 ctv1))
  (do))