(ns practice-clojure-dsl.values
  (:require [datomic.api :as d]
            [clojure.walk :as w]
            [practice-clojure-dsl.specs :refer [specify]]
            [practice-clojure-dsl.schemas :refer [schemify]]
            [practice-clojure-dsl.matcho :as m]))

(defn identify
  [val]
  (w/postwalk (fn [val]
                (if (and (map? val) (nil? (:val/id val)))
                  (assoc val :val/id (d/squuid))
                  val))
              val))

(comment
  (identify {:val/label "T1"})
  (do))

(defn check-in
  [val ty]
  (let [mismatches (m/match* val (specify ty))]
    (if (empty? mismatches)
      {:val/eq [val]
       :val/in [ty]}
      mismatches)))

(comment
  (let [v {:a ["a"]}
        t {:val/specs [v]
           :val/sort :val/type.simple}]
    (check-in v t))
  (do))

(defmulti register (fn [val _] (:val/sort val)))

(defmethod register :default
  [val conn]
  (let [val' (identify val)]
    @(d/transact conn (schemify val' []))
    @(d/transact conn [val'])
    val'))

(comment
  (do))

; 0. что с идентификаторами?
;    можно попробовать обойтись без :db/unique
; 1. проброс хранилища
; 2. на входе сущности или просто идентификаторы?
; 3. на выходе?
; 4. встроенные сущности?
; 5. конструкции, отношения, места?
