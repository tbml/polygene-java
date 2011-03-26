package org.qi4j.samples.dddsample.domain.model.cargo.assembly;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import org.qi4j.api.service.ServiceFinder;
import org.qi4j.api.service.ServiceReference;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.test.performance.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;
import org.qi4j.samples.dddsample.domain.model.cargo.Cargo;
import org.qi4j.samples.dddsample.domain.model.cargo.CargoRepository;
import org.qi4j.samples.dddsample.domain.model.carrier.assembly.CarrierMovementModelAssembler;
import org.qi4j.samples.dddsample.domain.model.handling.assembly.HandlingEventModelAssembler;
import org.qi4j.samples.dddsample.domain.model.location.LocationRepository;
import org.qi4j.samples.dddsample.domain.model.location.assembly.LocationModelAssembler;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;
import org.qi4j.test.AbstractQi4jTest;

import static org.junit.Assert.*;
import static org.qi4j.samples.dddsample.domain.model.cargo.assembly.SampleCargoDataBootstrapService.*;

public class CargoTest
    extends AbstractQi4jTest
{
    @Test
    public void testlastKnownLocationUnknownWhenNoEvents()
    {
        UnitOfWorkFactory uowf = moduleInstance.unitOfWorkFactory();
        UnitOfWork uow = uowf.newUnitOfWork();

        ServiceReference<CargoRepository> cargoRepositoryRef = cargoRepositoryService();
        CargoRepository cargoRepository = cargoRepositoryRef.get();

        ServiceReference<LocationRepository> locationRepositoryRef = locationRepositoryService();
        LocationRepository locationRepository = locationRepositoryRef.get();

        try
        {
            Cargo cargo = cargoRepository.find( XYZ );
            assertEquals( locationRepository.unknownLocation(), cargo.lastKnownLocation() );
        }
        finally
        {
            uow.discard();
        }
    }

    private ServiceReference<CargoRepository> cargoRepositoryService()
    {
        ServiceFinder serviceFinder = moduleInstance.serviceFinder();
        return serviceFinder.findService( CargoRepository.class );
    }

    private ServiceReference<LocationRepository> locationRepositoryService()
    {
        ServiceFinder serviceFinder = moduleInstance.serviceFinder();
        return serviceFinder.findService( LocationRepository.class );
    }

//    public void testlastKnownLocationReceived() throws Exception
//    {
//        Cargo cargo = populateCargoReceivedStockholm();
//
//        assertEquals( STOCKHOLM, cargo.lastKnownLocation() );
//    }
//
//    public void testlastKnownLocationClaimed() throws Exception
//    {
//        Cargo cargo = populateCargoClaimedMelbourne();
//
//        assertEquals( MELBOURNE, cargo.lastKnownLocation() );
//    }
//
//    public void testlastKnownLocationUnloaded() throws Exception
//    {
//        Cargo cargo = populateCargoOffHongKong();
//
//        assertEquals( HONGKONG, cargo.lastKnownLocation() );
//    }
//
//    public void testlastKnownLocationloaded() throws Exception
//    {
//        Cargo cargo = populateCargoOnHamburg();
//
//        assertEquals( HAMBURG, cargo.lastKnownLocation() );
//    }
//
//    public void testAtFinalLocation() throws Exception
//    {
//        Cargo cargo = populateCargoOffMelbourne();
//
//        assertTrue( cargo.hasArrived() );
//    }
//
//    public void testNotAtFinalLocationWhenNotUnloaded() throws Exception
//    {
//        Cargo cargo = populateCargoOnHongKong();
//
//        assertFalse( cargo.hasArrived() );
//    }
//
//    public void testEquality() throws Exception
//    {
//        Cargo c1 = new Cargo( new TrackingId( "ABC" ), STOCKHOLM, HONGKONG );
//        Cargo c2 = new Cargo( new TrackingId( "CBA" ), STOCKHOLM, HONGKONG );
//        Cargo c3 = new Cargo( new TrackingId( "ABC" ), STOCKHOLM, MELBOURNE );
//        Cargo c4 = new Cargo( new TrackingId( "ABC" ), STOCKHOLM, HONGKONG );
//
//        assertTrue( "Cargos should be equal when TrackingIDs are equal", c1.equals( c4 ) );
//        assertTrue( "Cargos should be equal when TrackingIDs are equal", c1.equals( c3 ) );
//        assertTrue( "Cargos should be equal when TrackingIDs are equal", c3.equals( c4 ) );
//        assertFalse( "Cargos are not equal when TrackingID differ", c1.equals( c2 ) );
//    }
//
//    protected void setUp() throws Exception
//    {
//        events = new HashSet<HandlingEvent>();
//    }
//
//    public void testIsUnloadedAtFinalDestination() throws Exception
//    {
//        assertFalse( new Cargo().isUnloadedAtDestination() );
//
//        Cargo cargo = setUpCargoWithItinerary( HANGZOU, TOKYO, NEWYORK );
//        assertFalse( cargo.isUnloadedAtDestination() );
//
//        // Adding an event unrelated to unloading at final destination
//        events.add(
//            new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU ) );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertFalse( cargo.isUnloadedAtDestination() );
//
//        CarrierMovement cm1 = new CarrierMovement( new CarrierMovementId( "CM1" ), HANGZOU, NEWYORK, new Date(), new Date() );
//
//        // Adding an unload event, but not at the final destination
//        events.add(
//            new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, TOKYO, cm1 ) );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertFalse( cargo.isUnloadedAtDestination() );
//
//        // Adding an event in the final destination, but not unload
//        events.add(
//            new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.CUSTOMS, NEWYORK ) );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertFalse( cargo.isUnloadedAtDestination() );
//
//        // Finally, cargo is unloaded at final destination
//        events.add(
//            new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, NEWYORK, cm1 ) );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertTrue( cargo.isUnloadedAtDestination() );
//    }
//
//    /* JAVADOC implement nextExpectedEvent
//    public void testNextExpectedEvent() {
//      Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
//      CarrierMovementId cmid = new CarrierMovementId("CM1");
//      CarrierMovement cm1 = new CarrierMovement(cmid, HANGZOU, TOKYO);
//      CarrierMovement cm2 = new CarrierMovement(cmid, TOKYO, NEWYORK);
//
//      HandlingEvent event1 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU, null);
//
//      assertEquals(event1, cargo.nextExpectedEvent());
//
//      cargo.deliveryHistory().addEvent(event1);
//
//      HandlingEvent event2 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, HANGZOU, cm1);
//
//      assertEquals(event2, cargo.nextExpectedEvent());
//
//      cargo.deliveryHistory().addEvent(event2);
//    }
//    */
//
//    // TODO: Generate test data some better way
//
//    private Cargo populateCargoReceivedStockholm() throws Exception
//    {
//        final Cargo cargo = new Cargo( new TrackingId( "XYZ" ), STOCKHOLM, MELBOURNE );
//
//        HandlingEvent he = new HandlingEvent( cargo, getDate( "2007-12-01" ), new Date(), HandlingEvent.Type.RECEIVE, STOCKHOLM );
//        events.add( he );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//
//        return cargo;
//    }
//
//    private Cargo populateCargoClaimedMelbourne() throws Exception
//    {
//        final Cargo cargo = populateCargoOffMelbourne();
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-09" ), new Date(), HandlingEvent.Type.CLAIM, MELBOURNE ) );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//
//        return cargo;
//    }
//
//    private Cargo populateCargoOffHongKong() throws Exception
//    {
//        final Cargo cargo = new Cargo( new TrackingId( "XYZ" ), STOCKHOLM, MELBOURNE );
//
//        final CarrierMovement stockholmToHamburg = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), STOCKHOLM, HAMBURG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-01" ), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, stockholmToHamburg ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-02" ), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, stockholmToHamburg ) );
//
//        final CarrierMovement hamburgToHongKong = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HAMBURG, HONGKONG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-03" ), new Date(), HandlingEvent.Type.LOAD, HAMBURG, hamburgToHongKong ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-04" ), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, hamburgToHongKong ) );
//
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        return cargo;
//    }
//
//    private Cargo populateCargoOnHamburg() throws Exception
//    {
//        final Cargo cargo = new Cargo( new TrackingId( "XYZ" ), STOCKHOLM, MELBOURNE );
//
//        final CarrierMovement stockholmToHamburg = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), STOCKHOLM, HAMBURG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-01" ), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, stockholmToHamburg ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-02" ), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, stockholmToHamburg ) );
//
//        final CarrierMovement hamburgToHongKong = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HAMBURG, HONGKONG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-03" ), new Date(), HandlingEvent.Type.LOAD, HAMBURG, hamburgToHongKong ) );
//
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        return cargo;
//    }
//
//    private Cargo populateCargoOffMelbourne() throws Exception
//    {
//        final Cargo cargo = new Cargo( new TrackingId( "XYZ" ), STOCKHOLM, MELBOURNE );
//
//        final CarrierMovement stockholmToHamburg = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), STOCKHOLM, HAMBURG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-01" ), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, stockholmToHamburg ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-02" ), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, stockholmToHamburg ) );
//
//        final CarrierMovement hamburgToHongKong = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HAMBURG, HONGKONG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-03" ), new Date(), HandlingEvent.Type.LOAD, HAMBURG, hamburgToHongKong ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-04" ), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, hamburgToHongKong ) );
//
//        final CarrierMovement hongKongToMelbourne = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HONGKONG, MELBOURNE, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-05" ), new Date(), HandlingEvent.Type.LOAD, HONGKONG, hongKongToMelbourne ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-07" ), new Date(), HandlingEvent.Type.UNLOAD, MELBOURNE, hongKongToMelbourne ) );
//
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        return cargo;
//    }
//
//    private Cargo populateCargoOnHongKong() throws Exception
//    {
//        final Cargo cargo = new Cargo( new TrackingId( "XYZ" ), STOCKHOLM, MELBOURNE );
//
//        final CarrierMovement stockholmToHamburg = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), STOCKHOLM, HAMBURG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-01" ), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, stockholmToHamburg ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-02" ), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, stockholmToHamburg ) );
//
//        final CarrierMovement hamburgToHongKong = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HAMBURG, HONGKONG, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-03" ), new Date(), HandlingEvent.Type.LOAD, HAMBURG, hamburgToHongKong ) );
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-04" ), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, hamburgToHongKong ) );
//
//        final CarrierMovement hongKongToMelbourne = new CarrierMovement(
//            new CarrierMovementId( "CAR_001" ), HONGKONG, MELBOURNE, new Date(), new Date() );
//
//        events.add( new HandlingEvent( cargo, getDate( "2007-12-05" ), new Date(), HandlingEvent.Type.LOAD, HONGKONG, hongKongToMelbourne ) );
//
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        return cargo;
//    }
//
//    public void testIsMisdirected() throws Exception
//    {
//        //A cargo with no itinerary is not misdirected
//        Cargo cargo = new Cargo( new TrackingId( "TRKID" ), SHANGHAI, GOTHENBURG );
//        assertFalse( cargo.isMisdirected() );
//
//        cargo = setUpCargoWithItinerary( SHANGHAI, ROTTERDAM, GOTHENBURG );
//
//        //A cargo with no handling events is not misdirected
//        assertFalse( cargo.isMisdirected() );
//
//        Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();
//
//        CarrierMovement abc = new CarrierMovement( new CarrierMovementId( "ABC" ), SHANGHAI, ROTTERDAM, new Date(), new Date() );
//        CarrierMovement def = new CarrierMovement( new CarrierMovementId( "DEF" ), ROTTERDAM, GOTHENBURG, new Date(), new Date() );
//        CarrierMovement ghi = new CarrierMovement( new CarrierMovementId( "GHI" ), ROTTERDAM, NEWYORK, new Date(), new Date() );
//
//        //Happy path
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 10 ), new Date( 20 ), HandlingEvent.Type.RECEIVE, SHANGHAI ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 30 ), new Date( 40 ), HandlingEvent.Type.LOAD, SHANGHAI, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 50 ), new Date( 60 ), HandlingEvent.Type.UNLOAD, ROTTERDAM, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 70 ), new Date( 80 ), HandlingEvent.Type.LOAD, ROTTERDAM, def ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 90 ), new Date( 100 ), HandlingEvent.Type.UNLOAD, GOTHENBURG, def ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 110 ), new Date( 120 ), HandlingEvent.Type.CLAIM, GOTHENBURG ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 130 ), new Date( 140 ), HandlingEvent.Type.CUSTOMS, GOTHENBURG ) );
//
//        events.addAll( handlingEvents );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertFalse( cargo.isMisdirected() );
//
//        //Try a couple of failing ones
//
//        cargo = setUpCargoWithItinerary( SHANGHAI, ROTTERDAM, GOTHENBURG );
//        handlingEvents = new ArrayList<HandlingEvent>();
//
//        handlingEvents.add( new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU ) );
//        events.addAll( handlingEvents );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertTrue( cargo.isMisdirected() );
//
//
//        cargo = setUpCargoWithItinerary( SHANGHAI, ROTTERDAM, GOTHENBURG );
//        handlingEvents = new ArrayList<HandlingEvent>();
//
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 10 ), new Date( 20 ), HandlingEvent.Type.RECEIVE, SHANGHAI ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 30 ), new Date( 40 ), HandlingEvent.Type.LOAD, SHANGHAI, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 50 ), new Date( 60 ), HandlingEvent.Type.UNLOAD, ROTTERDAM, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 70 ), new Date( 80 ), HandlingEvent.Type.LOAD, ROTTERDAM, ghi ) );
//
//        events.addAll( handlingEvents );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertTrue( cargo.isMisdirected() );
//
//
//        cargo = setUpCargoWithItinerary( SHANGHAI, ROTTERDAM, GOTHENBURG );
//        handlingEvents = new ArrayList<HandlingEvent>();
//
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 10 ), new Date( 20 ), HandlingEvent.Type.RECEIVE, SHANGHAI ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 30 ), new Date( 40 ), HandlingEvent.Type.LOAD, SHANGHAI, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date( 50 ), new Date( 60 ), HandlingEvent.Type.UNLOAD, ROTTERDAM, abc ) );
//        handlingEvents.add( new HandlingEvent( cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM ) );
//
//        events.addAll( handlingEvents );
//        cargo.setDeliveryHistory( new DeliveryHistory( events ) );
//        assertTrue( cargo.isMisdirected() );
//    }
//
//    /*
//    public void testStateOnCreation() {
//      final Cargo cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, CHICAGO);
//
//      final State state = cargo.state();
//      assertEquals(Location.UNKNOWN, state.currentLocation());
//      assertNull(state.currentCarrierMovement());
//      assertEquals(StatusCode.NOT_RECEIVED, state.statusCode());
//    }
//    */
//
//    private Cargo setUpCargoWithItinerary( Location origin, Location midpoint, Location destination )
//    {
//        Cargo cargo = new Cargo( new TrackingId( "CARGO1" ), origin, destination );
//
//        CarrierMovement cm = new CarrierMovement(
//            new CarrierMovementId( "ABC" ), origin, destination, new Date(), new Date() );
//
//        Itinerary itinerary = new Itinerary(
//            Arrays.asList(
//                new Leg( cm, origin, midpoint ),
//                new Leg( cm, midpoint, destination )
//            )
//        );
//
//        cargo.attachItinerary( itinerary );
//        return cargo;
//    }

    /**
     * Parse an ISO 8601 (YYYY-MM-DD) String to Date
     *
     * @param isoFormat String to parse.
     *
     * @return Created date instance.
     *
     * @throws java.text.ParseException Thrown if parsing fails.
     */
    private Date getDate( String isoFormat )
        throws ParseException
    {
        final DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        return dateFormat.parse( isoFormat );
    }

    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        module.services(
            MemoryEntityStoreService.class,
            UuidIdentityGeneratorService.class
        );
        new RdfMemoryStoreAssembler().assemble( module );
        new LocationModelAssembler( true ).assemble( module );
        new CarrierMovementModelAssembler( true ).assemble( module );
        new HandlingEventModelAssembler().assemble( module );
        new CargoModelAssembler( true ).assemble( module );
    }
}