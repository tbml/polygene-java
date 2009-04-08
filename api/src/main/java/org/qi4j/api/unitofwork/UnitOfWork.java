/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.qi4j.api.unitofwork;

import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.entity.LifecycleException;
import org.qi4j.api.query.QueryBuilderFactory;
import org.qi4j.api.usecase.Usecase;

/**
 * All operations on entities goes through an UnitOfWork.
 * <p>A UnitOfWork allows you to access
 * Entities and work with them. All modifications to Entities are recorded by the UnitOfWork,
 * and at the end they may be sent to the underlying EntityStore by calling complete(). If the
 * UoW was read-only you may instead simply discard() it.
 * </p>
 * <p>
 * A UoW differs from a traditional Transaction in the sense that it is not tied at all to the underlying
 * storage resource. Because of this there is no timeout on a UoW. It can be very short or very long.
 * Another difference is that if a call to complete() fails, and the cause is validation errors in the
 * Entities of the UoW, then these can be corrected and the UoW retried. By contrast, when a Transaction
 * commit fails, then the whole transaction has to be done from the beginning again.
 * <p>
 * UoW's can be nested easily. The changes done in the nested UoW do not show up in the parent UoW until
 * complete() is called, at which point the changes are merged with the parent UoW. If a nested UoW is
 * discarded then all of the changes made in the nested UoW will be thrown away.
 * </p>
 * <p>
 * A UoW can be associated with a Usecase. A Usecase describes the metainformation about the process
 * to be performed by the UoW. It contains settings such as CAP-guarantees and what state is going to be used
 * by the Usecase, which helps eager loading.
 * </p>
 */
public interface UnitOfWork
{

    /**
     * Get the UnitOfWorkFactory that this UnitOfWork was created from.
     *
     * @return The UnitOfWorkFactory instance that was used to create this UnitOfWork.
     */
    UnitOfWorkFactory unitOfWorkFactory();

    /**
     * Get the Usecase for this UnitOfWork
     *
     * @return the Usecase
     */
    Usecase usecase();

    MetaInfo metaInfo();

    /**
     * Create a new Entity which implements the given mixin type. An EntityComposite
     * will be chosen according to what has been registered and the visibility rules
     * for Modules and Layers will be considered. If several
     * mixins implement the type then an AmbiguousTypeException will be thrown.
     *
     * The identity of the Entity will be generated by the IdentityGenerator of the Module of the EntityComposite.
     *
     * @param type the mixin type that the EntityComposite must implement
     * @return a new Entity
     * @throws NoSuchEntityException if no EntityComposite type of the given mixin type has been registered
     * @throws org.qi4j.api.entity.LifecycleException
     *                               if the entity cannot be created
     */
    <T> T newEntity( Class<T> type )
        throws EntityTypeNotFoundException, LifecycleException;

    /**
     * Create a new Entity which implements the given mixin type. An EntityComposite
     * will be chosen according to what has been registered and the visibility rules
     * for Modules and Layers will be considered. If several
     * mixins implement the type then an AmbiguousTypeException will be thrown.
     *
     * @param identity the identity of the new Entity
     * @param type     the mixin type that the EntityComposite must implement
     * @return a new Entity
     * @throws NoSuchEntityException if no EntityComposite type of the given mixin type has been registered
     * @throws LifecycleException    if the entity cannot be created
     */
    <T> T newEntity( String identity, Class<T> type )
        throws EntityTypeNotFoundException, LifecycleException;

    /**
     * Create a new EntityBuilder for an EntityComposite which implements the given mixin type. An EntityComposite
     * will be chosen according to what has been registered and the visibility rules
     * for Modules and Layers will be considered. If several
     * mixins implement the type then an AmbiguousTypeException will be thrown.
     *
     * @param type the mixin type that the EntityComposite must implement
     * @return a new Entity
     * @throws NoSuchEntityException if no EntityComposite type of the given mixin type has been registered
     * @throws LifecycleException
     */
    <T> EntityBuilder<T> newEntityBuilder( Class<T> type )
        throws EntityTypeNotFoundException;

    /**
     * Create a new EntityBuilder for an EntityComposite which implements the given mixin type. An EntityComposite
     * will be chosen according to what has been registered and the visibility rules
     * for Modules and Layers will be considered. If several
     * mixins implement the type then an AmbiguousTypeException will be thrown.
     *
     * @param identity the identity of the new Entity
     * @param type     the mixin type that the EntityComposite must implement
     * @return a new Entity
     * @throws NoSuchEntityException if no EntityComposite type of the given mixin type has been registered
     * @throws LifecycleException
     */
    <T> EntityBuilder<T> newEntityBuilder( String identity, Class<T> type )
        throws EntityTypeNotFoundException;

    /**
     * Find an Entity of the given mixin type with the give identity. This
     * method verifies that it exists by asking the underlying EntityStore.
     *
     * @param identity of the entity
     * @param type     of the entity
     * @return the entity
     * @throws EntityTypeNotFoundException if no entity type could be found
     */
    <T> T find( String identity, Class<T> type )
        throws EntityTypeNotFoundException, NoSuchEntityException;

    /**
     * Get a reference to an Entity of the given mixin type with the given identity.
     * This method does not guarantee that the returned Entity actually exists.
     *
     * @param identity of the entity
     * @param type     of the entity
     * @return the entity
     * @throws EntityTypeNotFoundException if no entity type could be found
     */
    <T> T getReference( String identity, Class<T> type )
        throws EntityTypeNotFoundException;

    /**
     * If you have a reference to an Entity from another
     * UnitOfWork and want to create a reference to it in this
     * UnitOfWork, then call this method.
     *
     * @param entity the Entity to be dereferenced
     * @return an Entity from this UnitOfWork
     * @throws EntityTypeNotFoundException if no entity type could be found
     */
    <T> T dereference( T entity )
        throws EntityTypeNotFoundException;

    /**
     * Refresh the state of a given Entity. Call this if you
     * suspect that the underlying state may have been changed. If the
     * state has not been changed, then nothing happens. Otherwise
     * all changes to it are lost, and the updated state is used instead.
     * Existing references to Properties and Associations of Entities
     * in the UnitOfWork will continue to be valid.
     *
     * @param entity to be refreshed
     * @throws UnitOfWorkException if the refresh fails
     */
    void refresh( Object entity )
        throws UnitOfWorkException;

    /**
     * Refresh the state of all Entities in this UnitOfWork. Call this
     * if you suspect that the underlying state of one or more Entities
     * may have been changed. If the state has not been changed, then nothing happens.
     * Otherwise all changes to it are lost, and the updated state is used instead.
     * Existing references to Properties and Associations of Entities
     * in the UnitOfWork will continue to be valid.
     */
    void refresh();

    /**
     * Check if the given Entity comes from this UnitOfWork.
     *
     * @param entity the Entity to be checked
     * @return true if the Entity comes from this UnitOfWork
     */
    boolean contains( Object entity );

    /**
     * Clear this UnitOfWork. All Entities are removed, and all existing
     * references to Entities in this UnitOfWork must be dropped.
     */
    void reset();

    /**
     * Remove the given Entity.
     *
     * @param entity the Entity to be removed.
     * @throws LifecycleException if the entity could not be removed
     */
    void remove( Object entity )
        throws LifecycleException;

    /**
     * Complete this UnitOfWork. This will send all the changes down to the underlying
     * EntityStore's.
     * <p/>
     * If the complete fails due to validation errors or concurrent modifications,
     * then it is possible to refresh the Entities that were modified, and correct
     * the validation errors, and try again. This method can be called as many times
     * as necessary. After completion this UnitOfWork becomes invalid.
     *
     * @throws UnitOfWorkCompletionException if the UnitOfWork could not be completed
     * @throws ConcurrentEntityModificationException
     *                                       if entities have been modified by others
     */
    void complete()
        throws UnitOfWorkCompletionException, ConcurrentEntityModificationException;

    /**
     * Apply the changes in this UnitOfWork. This will send all the changes down to the underlying
     * EntityStores, without making this UnitOfWork invalid.
     *
     * @throws UnitOfWorkCompletionException if the changes could not be applied
     * @throws ConcurrentEntityModificationException
     *                                       if entities have been modified by others
     */
    void apply()
        throws UnitOfWorkCompletionException, ConcurrentEntityModificationException;

    /**
     * Discard thie UnitOfWork. Use this if a failure occurs that you cannot handle,
     * or if the usecase was of a read-only character.
     */
    void discard();

    /**
     * Check if the UnitOfWork is open. It is closed after either complete() or discard()
     * methods have been called successfully.
     *
     * @return true if the UnitOfWork is open.
     */
    boolean isOpen();

    /**
     * Check if the UnitOfWork is paused. It is not paused after it has been create through the
     * UnitOfWorkFactory, and it can be paused by calling {@link #pause()} and then resumed by calling
     * {@link #resume()}.
     *
     * @return true if this UnitOfWork has been paused.
     */
    boolean isPaused();

    /**
     * Pauses this UnitOfWork.
     * <p>
     * Calling this method will cause the underlying UnitOfWork to become the current UnitOfWork until the
     * the resume() method is called. It is the client's responsibility not to drop the reference to this
     * UnitOfWork while being paused.
     * </p>
     */
    void pause();

    /**
     * Resumes this UnitOfWork to again become the current UnitOfWork.
     */
    void resume();

    /**
     * Get the QueryBuilderFactory for this UnitOfWork
     *
     * @return a factory
     */
    QueryBuilderFactory queryBuilderFactory();

    /**
     * Register a callback. Callbacks are invoked when the UnitOfWork
     * is completed or discarded.
     *
     * @param callback a callback to be registered with this UnitOfWork
     */
    void addUnitOfWorkCallback( UnitOfWorkCallback callback );

    void removeUnitOfWorkCallback( UnitOfWorkCallback callback );

    void addStateChangeVoter( StateChangeVoter voter );

    void removeStateChangeVoter( StateChangeVoter voter );

    void addStateChangeListener( StateChangeListener listener );

    void removeStateChangeListener( StateChangeListener listener );
}
