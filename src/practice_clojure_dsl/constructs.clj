(ns practice-clojure-dsl.constructs
  (:require [datomic.api :as d]
            [practice-clojure-dsl.specs :refer [specify]]
            [practice-clojure-dsl.matcho :as m]))

(defn check-in
  [val ty]
  (let [mismatches (m/match* val (specify ty))]
    (if (empty? mismatches)
      {:val/id (str (:val/id val) "-in-" (:val/id ty))
       :val/eq [val]
       :val/in [ty]}
      mismatches)))

; 1. проброс хранилища
; 2. на входе сущности или просто идентификаторы?
; 3. на выходе?
; 4. встроенные сущности?
; 5. конструкции, отношения, места?