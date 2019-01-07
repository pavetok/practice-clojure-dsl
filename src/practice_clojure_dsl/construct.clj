(ns practice-clojure-dsl.construct
  (:require [datomic.api :as d]
            [practice-clojure-dsl.spec :refer [specify]]
            [practice-clojure-dsl.matcho :as m]))

(defn check-in
  [val ty]
  (let [mismatches (m/match* val (specify ty))]
    (if (empty? mismatches)
      {:val/id (str (:val/id val) "-in-" (:val/id ty))
       :val/eq [val]
       :val/in [ty]}
      mismatches)))
