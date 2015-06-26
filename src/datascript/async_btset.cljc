(ns datascript.async-btset
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go go-loop]]))
  (:require
    #?(:cljs [cljs.core.async :refer [<! put! chan]]
      :clj  [clojure.core.async :refer [<! put! chan go go-loop]])))

(defn btset-by [cmp & keys]
  (apply sorted-set-by cmp keys))

(defn btset [& keys]
  (apply sorted-set keys))

(defn- distinct-consecutive [sequence]
  (map first (partition-by identity sequence)))

(defn slice
  "When called with single key, returns iterator over set that contains all elements equal to the key.
   When called with two keys (range), returns iterator for all X where key-from <= X <= key-to"
  ([ss a] (slice ss a a))
  ([ss a b]
  (go
   ;; do subseq from !a -> a, and a -> b -> !b; concat and dedup
   ;; do overlaps (always <= or >=) since the glob matching needs it.
   (-> (rsubseq ss >= a >= a)
       (reverse)
       (concat (subseq ss >= a <= b))
       (distinct-consecutive)))))

;; constrain these versions to a single key, since protocol uses 2nd arg, and we ignore it.
(defn btset-conj [set key _] (conj set key))
(defn btset-disj [set key _] (disj set key))

(defn -btset-from-sorted-arr [_ _])